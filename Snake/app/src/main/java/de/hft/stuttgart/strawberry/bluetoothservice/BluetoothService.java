package de.hft.stuttgart.strawberry.bluetoothservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import de.hft.stuttgart.strawberry.activities.GPMultiActivity;
import de.hft.stuttgart.strawberry.common.Constants;
import de.hft.stuttgart.strawberry.snake.R;

/**
 * Dieser Service stellt die Verbindung mit dem Mitspieler her.
 * Er beinhaltet drei innere Threads.<br>
 * Der {@link AcceptConnectionThread} stellt einen ServerSocket
 * her auf beiden Geraeten, die damit in den "Empfangsmodus" gehen.<br>
 * Der {@link ConnectDevicesThread} ist die Clientseite, der die Anfrage
 * zum Verbinden an den ServerSocket des anderen Geraetes sendet. <br>
 * Der {@link ConnectionManagerThread} verwaltet die Kommunikation
 * zwischen den zweit verbundenen Geraeten
 */
public class BluetoothService {

    // TAG fuer den Logger
    private static final String TAG = BluetoothService.class.getSimpleName();

    // Name fuer sichere und unsichere Verbindung
    private static final String NAME_SECURE = "BluetoothSecure";
    private static final String NAME_INSECURE = "BluetoothInsecure";

    // UUID dieser App bei sicherer und unsicherer Verbindung
    private static final UUID MY_UUID_SECURE = UUID.fromString("1f54e3e7-9a1b-4e5b-bffb-88de8f59d36b");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("e066c0b3-c3f4-484d-9042-e0f428531e86");

    // Konstanten welche den Zustand der Verbindung beschreiben
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    // Die Threads als Objekte
    private AcceptConnectionThread secureAcceptConnectionThread;
    private AcceptConnectionThread insecureAcceptConnectionThread;
    private ConnectDevicesThread connectDevicesThread;
    private ConnectionManagerThread connectionManagerThread;

    // MultiplayerActivity
    GPMultiActivity multiActivity;

    // BT Adapter
    private BluetoothAdapter mBTAdapter;

    // Handler
    private Handler mHandler;

    // Verbindungsstatus
    private int mState;


    /*
    Konstruktor fuer diesen Service
     */
    public BluetoothService(Context context, Handler handler, BluetoothAdapter adapter) {
        mBTAdapter = adapter;
        mState = STATE_NONE;
        mHandler = handler;

        if (context instanceof GPMultiActivity) {
            multiActivity = (GPMultiActivity) context;
        }

    }

    /*
    Startet den Aufbau der Bluetoothverbindung
     */
    public synchronized void startConnection() {
        Log.d(TAG, "Start/Restart connection");

        // Thread zum Verbindungsaufbau initialisieren
        if (connectDevicesThread != null) {
            connectDevicesThread.cancel();
            connectDevicesThread = null;
        }

        // Thread zur Verbindungsverwaltung initialisieren
        if (connectionManagerThread != null) {
            connectionManagerThread.cancel();
            connectionManagerThread = null;
        }

        setState(STATE_LISTEN);

        // Starte Thread fuer eingehende Verbindungen
        if (secureAcceptConnectionThread == null) {
            secureAcceptConnectionThread = new AcceptConnectionThread(true);
            Thread secureThread = new Thread(secureAcceptConnectionThread);
            secureThread.start();
        }

        if(insecureAcceptConnectionThread == null) {
            insecureAcceptConnectionThread = new AcceptConnectionThread(false);
            Thread insecureThread = new Thread(insecureAcceptConnectionThread);
            insecureThread.start();
        }
    }

    /*
    Verbindung zum Geraet aufbauen
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect to: " + device.getName());

        // Thread zur Verbindungsaufbau initialisieren, falls laeuft
        if (mState == STATE_CONNECTING) {
            if (connectDevicesThread != null) {
                connectDevicesThread.cancel();
                connectDevicesThread = null;
            }
        }

        // Thread zur Verbindungsverwaltung initialisieren, falls laeuft
        if (connectionManagerThread != null) {
            connectionManagerThread.cancel();
            connectionManagerThread = null;
        }

        // starte Thread zum Verbindungsaufbau
        connectDevicesThread = new ConnectDevicesThread(device, secure);
        Thread connectThread = new Thread(connectDevicesThread);
        connectThread.start();

        setState(STATE_CONNECTING);
    }

    /*
    Verbindung verwalten
     */
    public synchronized void manageConnection(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        Log.d(TAG, "manageConnection(), Socket Type:" + socketType);

        // Thread zum Verbindungsaufbau schliessen
        if (connectDevicesThread != null) {
            connectDevicesThread.cancel();
            connectDevicesThread = null;
        }

        // Threads welche eine Verbindung haben schliessen
        if (connectionManagerThread != null) {
            connectionManagerThread.cancel();
            connectionManagerThread = null;
        }

        // Thread fuer eingehende Verbindungsanfragen auch schliessen
        if (secureAcceptConnectionThread != null) {
            secureAcceptConnectionThread.cancel();
            secureAcceptConnectionThread = null;
        }
        if (insecureAcceptConnectionThread != null) {
            insecureAcceptConnectionThread.cancel();
            insecureAcceptConnectionThread = null;
        }

        // Thread zur Verwaltung der Verbindung starten
        connectionManagerThread = new ConnectionManagerThread(socket, socketType);
        Thread managerThread = new Thread(connectionManagerThread);
        managerThread.start();

        // Den Namen des verbundenen Geraetes an das UI senden
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
        Log.d(TAG, "connected with device: " + device.getName());
    }

    /*
    Alle Threads stoppen
     */
    public synchronized void stop() {
        Log.d(TAG, "stopping all service threads");

        // Thread zum Verbindungsaufbau schliessen
        if (connectDevicesThread != null) {
            connectDevicesThread.cancel();
            connectDevicesThread = null;
        }

        // Threads welche eine Verbindung haben schliessen
        if (connectionManagerThread != null) {
            connectionManagerThread.cancel();
            connectionManagerThread = null;
        }

        // Thread fuer eingehende Verbindungsanfragen auch schliessen
        if (secureAcceptConnectionThread != null) {
            secureAcceptConnectionThread.cancel();
            secureAcceptConnectionThread = null;
        }

        if (insecureAcceptConnectionThread != null) {
            insecureAcceptConnectionThread.cancel();
            insecureAcceptConnectionThread = null;
        }

        setState(STATE_NONE);
        Log.d(TAG, "all service threads stopped");
    }

    /*
    Nachrichten uebertragen
     */
    public void write(byte[] out) {
        // Tmp Thread Objekt
        ConnectionManagerThread cm;

        // Eine Kopie des richtigen ConnectionManager Threads
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            cm = connectionManagerThread;
        }

        // Datenuebertragung unsynchronisiert
        Log.d(TAG, "call write() from BluetoothService ");
        cm.write(out);
    }

    /*
    Die Position uebertragen
     */
    public void writePosition(byte[] out) {

        // Tmp Thread Objekt
        ConnectionManagerThread cm;

        // Eine Kopie des richtigen ConnectionManager Threads
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            cm = connectionManagerThread;
        }

        // Datenuebertragung unsynchronisiert TODO vielleicht in try catch block
        cm.write(out);
    }

    /*
    Nachricht an das UI, dass die Verbindung nicht aufgebaut werden konnte
    und Neustart zum Verbindungsaufbau.
     */
    public void connectionFailed() {

        // Message holen
        final String UIMsg = multiActivity.getString(R.string.could_not_connect);

        // Fehlermeldung an die Activity senden
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, UIMsg);
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        multiActivity.setActiveConnection(false);

        // Service wieder in den Horchmodus
        Log.d(TAG, "startConnection() from connectionFailed()");
        BluetoothService.this.startConnection();
    }

    /*
    Nachricht an das UI, dass die Verbindung unterbrochen wurde
    und Neustart zum Verbindungsaufbau.
     */
    public void connectionLost() {

        // Fehlermeldung an die Activity senden
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Verbindung wurde getrennt");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        multiActivity.setActiveConnection(false);

        // Service wieder in den Horchmodus
        Log.d(TAG, "startConnection() from connectionLost()");
        BluetoothService.this.startConnection();
    }

    /*
    Getter fuer den Status
     */
    public synchronized int getState() {
        return mState;
    }

    /*
    Setter fuer den Status der aktuellen Verbindung
     */
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // den neuen Status an die Activity weitergeben, fuer UI Aktualisierung
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Dieser AcceptConnectionThread oeffnet einen Kanal auf einem
     * ServerSocket und horcht auf eingehende Verbindungsanfragen.
     */
    public class AcceptConnectionThread implements Runnable {

        // Server-Socket fuer eingehende Verbindungsanfragen
        private final BluetoothServerSocket mServerSocket;

        // beschreibt den Verbindungstyp "sicher" | "unsicher"
        private String mSocketType;


        /*
        Konstruktor fuer diesen Thread
         */
        public AcceptConnectionThread(boolean secure) {

            // Verbindungstyp pruefen
            mSocketType = secure ? "Secure" : "Insecure";
            Log.i(TAG, "start Thread AcceptConnection with socketType: " + mSocketType);

            // temporaerer BluetoothServerSocket
            BluetoothServerSocket tmpServerSocket = null;

            // Erstellung einer horchenden Server-Sockets
            try {
                // fuer sichere Verbindung
                if (mSocketType.equals("Secure")) {
                    tmpServerSocket = mBTAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
                    // fuer unsichere Verbindung
                } else {
                    tmpServerSocket = mBTAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to create BluetoothServerSocket for Socket Type: " + mSocketType, e);
            }

            // ServerSocket uebergeben
            mServerSocket = tmpServerSocket;
        }

        /*
        Standard run() Methode fuer diesen Thread
         */
        public void run() {
            Log.d(TAG, "Begin AcceptThread with SocketType: " + mSocketType);

            // temporaerer BluetoothSocket
            BluetoothSocket btSocket;

            // Anfragen akzeptieren, solange noch keine Verbindung besteht
            while (mState != STATE_CONNECTED) {
                try {
                    btSocket = mServerSocket.accept();
                    Log.d(TAG, "Server returned btSocket: " + btSocket);
                } catch (IOException e) {
                    Log.e(TAG, "Error while executing accept()", e);
                    break;
                }

                // wenn eine Socket zurueck geliefert wurde
                if (btSocket != null) {
                    synchronized (AcceptConnectionThread.this) {
                        Log.d(TAG, "current State:  " + mState);
                        switch (mState) {
                            case STATE_LISTEN:
                                // einfach weiter
                            case STATE_CONNECTING:
                                setState(STATE_CONNECTING);
                                // starte ausgehende Verbindungen
                                manageConnection(btSocket, btSocket.getRemoteDevice(), mSocketType);
                                Log.d(TAG, "Ended AcceptBtConnection with startManager()");
                                break;
                            case STATE_NONE:
                                // einfach weiter
                                // Nach erfolgreicher Verbindung oder Abbruch den Socket wieder Schliessen
                            case STATE_CONNECTED:
                                try {
                                    btSocket.close();
                                    Log.d(TAG, "Ended AcceptBtConnection with closing the socket");
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close Socket", e);
                                }
                                break;
                        }
                    }
                    Log.d(TAG, "finished AcceptBTConnection.start()");
                }
            }
        }

        /*
        Schliesst den ServerSocket
         */
        public void cancel() {
            Log.d(TAG, "Begin cancel BtServerSocket Socket Type: " + mSocketType);
            try {
                mServerSocket.close();
                Log.i(TAG, "serverSocket closed");
            }catch (IOException e) {
                Log.e(TAG, "Error while cancelling BtServerSocket");
            }
        }
    }

    /**
     * Dieser ConnectDevicesThread oeffnet einen Kanal auf einem
     * BT-Socket und sendet eine Verbindungsanfrage an den
     * ServerSocket des anderen Geraetes.
     */
    public class ConnectDevicesThread implements Runnable {


        // Das Socket fuer die clientseitige Verbindung
        private BluetoothSocket mSocket;

        // Objekt, welches das Verbundene Geraet repraesentiert
        private final BluetoothDevice mDevice;

        // beschreibt den Verbindungstyp "sicher" | "unsicher"
        private String mSocketType;

        /*
        Standard Konstruktor fuer diesen Thread
         */
        public ConnectDevicesThread(BluetoothDevice device, boolean secure) {

            // Verbindungstyp pruefen
            mSocketType = secure ? "Secure" : "Insecure";
            Log.i(TAG, "start Thread ConnectDevices with socketType: " + mSocketType);

            // Remote-Geraet setzen
            mDevice = device;

            // temporaerer BluetoothSocket
            BluetoothSocket tmpBtSocket = null;

            // Verbindungskanal mit dem Remote-Geraet herstellen
            try {
                if (secure) {//TODO hier wird im Beispiel der temporaere device genutzt
                    tmpBtSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                } else {
                    tmpBtSocket = mDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);

                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to create Socket to device ", e);
            }

            // bei Erfolg Socket setzten
            mSocket = tmpBtSocket;
        }

        /*
        Standard run() Methode fuer diesen Thread
         */
        public void run() {
            Log.d(TAG, "ConnectDevice.start");

            // zuerst noch mal sicherstellen das die Suche nicht l?uft
            mBTAdapter.cancelDiscovery();

            // Verbindungsaufbau zum Socket
            try {
                mSocket.connect();
                Log.d(TAG, "connecting to device");
            } catch (IOException e) {
                Log.e(TAG, "unable to connect to Socket..", e);
                try {
                    Log.d(TAG, "trying fallback...");

                    if (mSocketType.equals("Secure")) {
                        Method methodSecure = mDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                        mSocket = (BluetoothSocket) methodSecure.invoke(mDevice, 1);
                    } else {
                        Method methodInsecure = mDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class});
                        mSocket = (BluetoothSocket) methodInsecure.invoke(mDevice, 1);
                    }
                    mSocket.connect();
                    mSocket.close();
                } catch (IOException ioe) {
                    Log.e(TAG, "fallback didn't work as well", ioe);
                } catch (NoSuchMethodException nsme) {
                    Log.e(TAG, "could not find called Method", nsme);
                } catch (IllegalAccessException iae) {
                    Log.e(TAG, "illegal access exception", iae);
                } catch (InvocationTargetException ite) {
                    Log.e(TAG, "invocation target exception", ite);
                }
                finally {
                    connectionFailed();
                }
            }

            // Connect-Thread zuruecksetzen, da fertig
            synchronized (BluetoothService.this) {
                connectDevicesThread = null;
                insecureAcceptConnectionThread = null;
                secureAcceptConnectionThread = null;
            }

            // ConnectionManager Thread starten, wenn Verbindung besteht
            manageConnection(mSocket, mDevice, mSocketType);
        }

        /*
        Socketverbindung trennen
         */
        public void cancel() {
            try {
                mSocket.close();
                Log.i(TAG, "socket closed and stopped trying to connect to device");
            } catch (IOException e) {
                Log.e(TAG, "Could not close socket to Bluetooth device", e);
            }
        }
    }

    /**
     * Dieser ConnectionManagerThread verwaltet waehrend einer
     * bestehenden Verbindung den Datenaustausch zwischen zwei
     * Verbundenen Geraeten.
     */
    public class ConnectionManagerThread implements Runnable {

        // Das Socket fuer die clientseitige Verbindung
        private final BluetoothSocket mSocket;

        // InputStream fuer eingehende Daten
        private final InputStream mInputStream;

        // OutputStream fuer eingehende Daten
        private final OutputStream mOutputStream;

        /*
        Standard-Konstruktor fuer diesen Thread
         */
        public ConnectionManagerThread(BluetoothSocket socket, String socketType) {
            Log.i(TAG, "start Thread ConnectionManager " + socketType);

            // Socket setzen
            mSocket = socket;

            // Temporaere Input-/ OutputStream initialisieren
            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;

            // Socketstreams holen
            try {
                tmpInputStream = mSocket.getInputStream();
                tmpOutputStream = mSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Could not getInputStream() | getOutputStream() ");
            }

            // bei Erfolg, Streams setzen
            mInputStream = tmpInputStream;
            mOutputStream = tmpOutputStream;
        }

        /*
        Standard run() Methode fuer diesen Thread
        TODO hier wird auch die Position des verbundenen Geraetes ausgelesen bzw versendet
         */
        public void run() {
            Log.d(TAG, "ConnectionManager.start()");
            // Byte-Array initialisieren TODO: Arraygroesse austeseten welche groesse benoetigt wird.
            byte[] buffer = new byte[Constants.STREAM_BUFFER_SIZE];

            // Bytelaenge aus den Streams
            int bytes;
            int locator;

            // Auf den InputStream horchen, so lange eine Verbindung besteht
            while (true) {
                try {
                    // Aus InputStream einlesen
                    bytes = mInputStream.read(buffer);

                    // Schickt alle Nachrichten an Messsage read
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    Log.w(TAG, "devices disconnected");
                    connectionLost();

                    // Start the service over to restart listening mode
                    BluetoothService.this.startConnection();
                    break;
                }
            }
        }

        /*
        In den OutputStream schreiben
         */
        public synchronized void write(byte[] buffer) {
            Log.d(TAG, "start write() from ConnectionThread");
            try {
                // Positionen senden
                mOutputStream.write(buffer);

                Log.d(TAG, "message or position sent to other device");
            } catch (IOException e) {
                Log.e(TAG, "could not send Position to other device", e);
            }
        }

        /*
        Socketverbindung trennen
         */
        public void cancel() {
            try {
                mSocket.close();
                Log.i(TAG, "socket closed and disconnected from device");
            } catch (IOException e) {
                Log.e(TAG, "Could not close socket to Bluetooth device", e);
            }
        }
    }
}
