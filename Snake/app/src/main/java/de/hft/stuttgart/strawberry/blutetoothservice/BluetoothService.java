package de.hft.stuttgart.strawberry.blutetoothservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import de.hft.stuttgart.strawberry.common.Constants;

/**
 * Dieser Service verwaltet die Bluetooth Verbindung und
 * kommuniziert mit der Multiplayer Activity
 */
public class BluetoothService {

    // TAG für den Logger
    private static final String TAG = BluetoothService.class.getSimpleName();

    // Konstanten, welche den Verbindungsstatus hinweisen
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    // Threads für die Bluetoothverbindung
    private AcceptBTConnection secureAcceptBTConnectionThread;
    private AcceptBTConnection insecureAcceptBTConnectionThread;
    private ConnectDevice connectDeviceThread;
    private ConnectionManager connectionManagerThread;

    // BT Adapter
    private BluetoothAdapter mBTAdapter;

    // Handler
    private Handler mHandler;

    // Zustand
    private int mState;

    /*
    Leer-Konstruktor für diesen Service
     */
    public BluetoothService() {
        // do nothing
    }

    /*
    Konstruktor für diesen Service
     */
    public BluetoothService(Context context, Handler handler) {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /*
    Startet den Aufbau der Bluetoothverbindung
     */
    public synchronized void startConnection() {
        Log.d(TAG, "Start BluetoothService Thread");

        // Thread zum Verbindungsaufbau initialisieren
        if (connectDeviceThread != null) {
            connectDeviceThread.cancel();
            connectDeviceThread = null;
        }

        // Thread zur Verbindungsverwaltung initialisieren
        if (connectionManagerThread != null) {
            connectionManagerThread.cancel();
            connectionManagerThread = null;
        }

        setState(STATE_LISTEN);

        // Starte Thread für eingehende Verbindungen
        if (secureAcceptBTConnectionThread == null) {
            secureAcceptBTConnectionThread = new AcceptBTConnection(true, mBTAdapter, this);
            secureAcceptBTConnectionThread.start();
        }

        if(insecureAcceptBTConnectionThread == null) {
            insecureAcceptBTConnectionThread = new AcceptBTConnection(true, mBTAdapter, this);
            insecureAcceptBTConnectionThread.start();
        }
    }

    /*
    Verbindung zum Gerät aufbauen
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (connectDeviceThread != null) {
                connectDeviceThread.cancel();
                connectDeviceThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (connectionManagerThread != null) {
            connectionManagerThread.cancel();
            connectionManagerThread = null;
        }

        // den vorhandenen Accept-Thread auswählen
        AcceptBTConnection acceptThread = null;
        if (secureAcceptBTConnectionThread != null) {
            acceptThread = secureAcceptBTConnectionThread;
        } else {
            acceptThread = insecureAcceptBTConnectionThread;
        }

        // Start the thread to connect with the given device
        connectDeviceThread = new ConnectDevice(device, secure, mBTAdapter, mHandler, this);
        connectDeviceThread.start();
        setState(STATE_CONNECTING);
    }

    /*
    Verbindung verwalten
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        Log.d(TAG, "connected, Socket Type:" + socketType);

        // Thread zum Verbindungsaufbau schließen
        if (connectDeviceThread != null) {
            connectDeviceThread.cancel();
            connectDeviceThread = null;
        }

        // Threads welche eine Verbindung haben schließen
        if (connectionManagerThread != null) {
            connectionManagerThread.cancel();
            connectionManagerThread = null;
        }

        // Thread für eingehende Verbindungsanfragen auch schließen
        if (secureAcceptBTConnectionThread != null) {
            secureAcceptBTConnectionThread.cancel();
            secureAcceptBTConnectionThread = null;
        }
        if (insecureAcceptBTConnectionThread != null) {
            insecureAcceptBTConnectionThread.cancel();
            insecureAcceptBTConnectionThread = null;
        }

        // Thread zur Verwaltung der Verbindung starten
        connectionManagerThread = new ConnectionManager(socket, socketType, mHandler);
        connectionManagerThread.start();

        // Den Namen des verbundenen Gerätes an das UI senden
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
        Log.d(TAG, "succuessfully connected to device: " + device.getName());
    }

    /*
    Alle Threads stoppen
     */
    public synchronized void stop() {
        Log.d(TAG, "stopping all service threads");

        // Thread zum Verbindungsaufbau schließen
        if (connectDeviceThread != null) {
            connectDeviceThread.cancel();
            connectDeviceThread = null;
        }

        // Threads welche eine Verbindung haben schließen
        if (connectionManagerThread != null) {
            connectionManagerThread.cancel();
            connectionManagerThread = null;
        }

        // Thread für eingehende Verbindungsanfragen auch schließen
        if (secureAcceptBTConnectionThread != null) {
            secureAcceptBTConnectionThread.cancel();
            secureAcceptBTConnectionThread = null;
        }

        if (insecureAcceptBTConnectionThread != null) {
            insecureAcceptBTConnectionThread.cancel();
            insecureAcceptBTConnectionThread = null;
        }

        setState(STATE_NONE);
    }

    /*
    Die Position übertragen
     */
    public void write(byte[] out) {

        // Tmp Thread Objekt
        ConnectionManager cm;

        // Eine Kopie des richtigen ConnectionManager Threads
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            cm = connectionManagerThread;
        }

        // Datenübertragung unsynchronisiert TODO vielleicht in try catch block
        cm.write(out);
    }

    /*
    Nachricht an das UI, dass die Verbindung nicht aufgebaut werden konnte
    und Neustart zum Verbindungsaufbau.
     */
    private void connectionFailed() {

        String UIMsg = "Verbindung mit dem Ger\\u00E4t herstellen";

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, UIMsg);
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothService.this.startConnection();
    }

    /*
    Nachricht an das UI, dass die Verbindung unterbrochen wurde
    und Neustrt zum Verbindungsaufbau.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothService.this.startConnection();
    }

    /*
    Schließt den Thread für eingehende Verbindungen
     */
    public void closeAcceptThread() {
        synchronized (BluetoothService.this) {
            if (secureAcceptBTConnectionThread != null) {
                secureAcceptBTConnectionThread = null;
            } else {
                insecureAcceptBTConnectionThread = null;
            }
        }
    }

    /*
    Getter für den Status
     */
    public synchronized int getState() {
        return mState;
    }

    /*
    Setter für den Status der aktuellen Verbindung
     */
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /*
    Getter für den ConnectionManager
     */
    public ConnectionManager getConnectionManagerThread() {
        return connectionManagerThread;
    }
}
