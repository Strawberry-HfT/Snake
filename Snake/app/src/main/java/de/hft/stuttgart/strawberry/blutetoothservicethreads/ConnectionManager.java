package de.hft.stuttgart.strawberry.blutetoothservicethreads;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.hft.stuttgart.strawberry.common.Constants;

/**
 * Dieser Thread wird während einer bestehenden Verbidung ausgeführt, um den
 * Nachrichtenaustausch mit dem verbundenen Bluetooth Gerät zu verwalten.
 * Da read(byte[]) und write(byte[]) blockierende Aufrufe sind, muss dieser Vorgang in einem
 * separaten Thread, als in der MainActivity ausgeführt werden.
 */
public class ConnectionManager extends Thread {

    // TAG für den Logger
    private static final String TAG = ConnectionManager.class.getSimpleName();

    // Das Socket für die clientseitige Verbindung
    private final BluetoothSocket mmSocket;

    // InputStream für eingehende Daten
    private final InputStream mmInputStream;

    // OutputStream für eingehende Daten
    private final OutputStream mmnOutputStream;

    // Handler
    private final Handler mmHandler;

    /*
    Standard-Konstruktor für diesen Thread
     */
    public ConnectionManager (BluetoothSocket socket, String socketType, Handler handler) {
        Log.d(TAG, "create Thread ConnectionManager");

        // Socket setzen
        mmSocket = socket;

        // Handler setzen
        mmHandler = handler;

        // Temporäre Input-/ OutputStream initialisieren
        InputStream tmpInputStream = null;
        OutputStream tmpOutputStream = null;

        // Socketstreams holen
        try {
            tmpInputStream = socket.getInputStream();
            tmpOutputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Could not getInputStream() | getOutputStream() ");
        }

        // bei Erfolg, Streams setzen
        mmInputStream = tmpInputStream;
        mmnOutputStream = tmpOutputStream;

    }

    /*
    Standard run() Methode für diesen Thread
    in dieser Methode wird auch die Position
    des verbundenen Gerätes ausgelesen
     */
    public void run() {
        Log.i(TAG, "Begin ConnectionManager");

        // Byte-Array initialisieren TODO: Arraygröße austeseten welche größe benötigt wird.
        byte[] buffer = new byte[1024];

        // Bytelänge aus den Streams
        int bytes;

        // Auf den InputStream horchen, so lange eine Verbindung besteht
        while (true) {
            try {
                // Aus InputStream einlesen
                bytes = mmInputStream.read(buffer);

                // erhaltene bytes an das UI senden TODO: brauchen wir hier nicht hab ich erst mal nur aus dem Beispiel
                mmHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "devices disconnected");
                connectionLost();

                // Start the service over to restart listening mode
                //TODO hier muss die Methode start() aufgerufen werden

                break;
            }
        }
    }

    /*
    In den OutputStream schreiben
     */
    public void write(byte[] buffer) {
        try {
            // Positionen senden
            mmnOutputStream.write(buffer);
            Log.i(TAG, "sent Positions to device");
        } catch (IOException e) {
            Log.e(TAG, "could not send Position to other device", e);
        }
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

    // TODO muss noch in die Activity ausgelagert werden, ist hier falsch
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mmHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mmHandler.sendMessage(msg);
    }


}
