package de.hft.stuttgart.strawberry.blutetoothservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Dieser Thread wird ausgeführt, um eingehende Verbindungen über Bluetooth
 * zu versuchen, bis eine Verbindung hergestellt oder abgebrochen wurde.
 * Da accept() eine blockierender Aufruf ist, muss dieser Vorgang
 * in einem separaten Thread, als in der MainActivity ausgeführt werden.
 */
public class AcceptBTConnection extends Thread {

    // TAG für den Logger
    private static final String TAG = AcceptBTConnection.class.getSimpleName();

    // Name für sichere und unsichere Verbindung
    private static final String NAME_SECURE = "BluetoothSecure";
    private static final String NAME_INSECURE = "BluetoothInsecure";

    // UUID dieser App bei sicherer und unsicherer Verbindung
    private static final UUID MY_UUID_SECURE = UUID.fromString("1f54e3e7-9a1b-4e5b-bffb-88de8f59d36b");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("e066c0b3-c3f4-484d-9042-e0f428531e86");

    // Konstanten welche den Zustand der Verbindung beschreiben
    private static final int STATE_NONE = 0;
    private static final int STATE_LISTEN = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;

    // Server-Socket für eingehende Verbindungsanfragen
    private final BluetoothServerSocket mmServerSocket;

    // beschreibt den Verbindungstyp "sicher" | "unsicher"
    private String mSocketType;

    // Bluetooth Service
    BluetoothService mService;

    private int mState;

    /*
    Konstruktor für diesen Thread
     */
    public AcceptBTConnection(boolean secure, BluetoothAdapter mBluetoothAdapter, BluetoothService service) {
        BluetoothServerSocket tmpServerSocket = null;

        // Verbindungstyp prüfen
        mSocketType = secure ? "Secure" : "Insecure";

        // Zustand initialisieren
        mState = STATE_NONE;

        // Service setzen
        mService = service;

        // Erstellung einer horchenden Server-Sockets
        try {
            // für sichere Verbindung
            if (secure) {
                tmpServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
            // für unsichere Verbindung
            } else {
                tmpServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to create BluetoothServerSocket for Socket Type: " + mSocketType, e);
        }

        // ServerSocket übergeben
        mmServerSocket = tmpServerSocket;
    }

    /*
    Standard run() Methode für diesen Thread
     */
    public void run() {

        Log.d(TAG, "Begin AcceptBTConnection");

        setName("AcceptBTConnection" + mSocketType);

        BluetoothSocket btSocket = null;

        // Anfragen aktzeptieren, solange noch keine Verbindung besteht
        while (mState != STATE_CONNECTED) {
            try {
                btSocket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Error while executing accept()", e);
            }
        }

        // wenn eine Socket zurück geliefert wurde
        if (btSocket != null) {
            synchronized (AcceptBTConnection.this) {
                switch (mState) {
                    case STATE_LISTEN:
                        // einfach weiter
                    case STATE_CONNECTING:
                        // starte ausgehende Verbindungen
                        mService.connected(btSocket, btSocket.getRemoteDevice(), mSocketType);
                        break;
                    case STATE_NONE:
                        // einfach weiter
                    // Nach erfolgreicher Verbindung oder Abbruch den Socket wieder Schließen
                    case STATE_CONNECTED:
                        try {
                            btSocket.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Could not close Socket", e);
                        }
                        break;
                }
            }
        }
        Log.d(TAG, "Ended AcceptBtConnection");
    }

    /*
    Schließt den ServerSocket
     */
    public void cancel() {
        Log.d(TAG, "Begin cancel BtServerSocket Socket Type: " + mSocketType);
        try {
            mmServerSocket.close();
        }catch (IOException e) {
            Log.e(TAG, "Error while cancelling BtServerSocket");
        }
    }

}
