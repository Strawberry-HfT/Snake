package de.hft.stuttgart.strawberry.blutetoothservicethreads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
;import de.hft.stuttgart.strawberry.common.Constants;

/**
 * Dieser Thread wird ausgeführt, um ausgehende Verbindungen über Bluetooth
 * zu versuchen, bis eine Verbindung hergestellt oder abgebrochen wurde.
 * Da connect() eine blockierender Aufruf ist, muss dieser Vorgang in einem
 * separaten Thread, als in der MainActivity ausgeführt werden.
 */
public class ConnectDevice extends Thread {

    // TAG für den Logger
    private static final String TAG = ConnectDevice.class.getSimpleName();

    // UUID dieser App bei sicherer und unsicherer Verbindung
    private static final UUID MY_UUID_SECURE = UUID.fromString("1f54e3e7-9a1b-4e5b-bffb-88de8f59d36b");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("1f54e3e7-9a1b-4e5b-zwxe-88de8f59d36b");

    // Das Socket für die clientseitige Verbindung
    private final BluetoothSocket mmSocket;

    // Objekt, welches das Verbundene Gerät repräsentiert
    private final BluetoothDevice mmDevice;

    // beschreibt den Verbindungstyp "sicher" | "unsicher"
    private String mmsocketType;

    // BluetoothAdapter
    private BluetoothAdapter mmBluetoothAdapter;

    // Handler
    private final Handler mmHandler;

    /*
    Standard Konstruktor für diesen Thread
     */
    public ConnectDevice (BluetoothDevice device, boolean secure, BluetoothAdapter mBluetoothAdapter, Handler handler) {

        // Remote-Gerät setzen
        mmDevice = device;

        // Adapter setzen
        mmBluetoothAdapter = mBluetoothAdapter;

        // Handler setzen
        mmHandler = handler;

        BluetoothSocket tmpBtSocket = null;

        // Verbindungstyp prüfen
        mmsocketType = secure ? "secure" : "unsecure";

        // Verbindungskanal mit dem Remote-Gerät herstellen
        try {
            if (secure) {
                tmpBtSocket = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            } else {
                tmpBtSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to create Socket to device ", e);
        }

        // bei Erfolg Socket setzten
        mmSocket = tmpBtSocket;
    }

    /*
    Standard run() Methode für diesen Thread
     */
    public void run() {
        Log.d(TAG, "Begin ConnectDevice");

        setName("ConnectDevice" + mmsocketType);

        // zuerst noch mal sicherstellen das die Suche nicht läuft
        mmBluetoothAdapter.cancelDiscovery();

        // Verbindungsaufbau zum Socket
        try {
            mmSocket.connect();
        } catch (IOException e) {
            Log.e(TAG, "unable to connect to Socket..", e);
            try {
                mmSocket.close();
            } catch (IOException ioe) {
                Log.e(TAG, "and also unable to close Socket..", ioe);
            }
            connectionFailed();
        }

        //TODO HIER WEITERMACHEN -> nächsten Thread erstellen und dann die Multiplayer Activity
        // Diesen Thread zurücksetzten, da fertig
        //TODO muss wohl in der Activity selbst gemacht werden
        // TODO siehe BluetoothChat -> BluetoothChatService Line 426

        // ConnectedDevice Thread starten
        //TODO im Beispiel wird hier der nächste Thread gestartet wir müssen das aber in der Activity
    }

    /*
    Socketverbindung trennen
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close socket to Bluetooth device", e);
        }
    }

    /*
    Fehlermeldung an das UI senden
     */
    // TODO muss noch in die Activity ausgelagert werden, ist hier falsch
    private void connectionFailed() {

        // Fehlermeldung an das UI senden
        Message msg = mmHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mmHandler.sendMessage(msg);

        // Wieder zum Anfang der Bluetoothverbindung
        //TODO hier muss die Mehtode start() aufgerufen werden

    }

}
