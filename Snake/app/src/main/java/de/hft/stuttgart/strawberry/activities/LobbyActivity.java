package de.hft.stuttgart.strawberry.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import de.hft.stuttgart.strawberry.bluetoothservice.BluetoothService;
import de.hft.stuttgart.strawberry.common.Constants;
import de.hft.stuttgart.strawberry.snake.R;

/**
 * In dieser Activity wird die Verbindung zwischen zwei Geraeten
 * hergestellt. Fuer den Verbindungsaufbau wird der
 * {@link BluetoothService} benutzt und bei aktiver Verbindung
 * startet der Multiplayer Modus.
 */
public class LobbyActivity extends Activity {

    // TAG fuer den Logger
    private static final String TAG = LobbyActivity.class.getSimpleName();

    // Konstanten, für verschiedene Status
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    public static final int REQUEST_DISCOVERABLE = 4;

    // Name des verbundenen Geraetes
    private String mConnectedDeviceName = null;

    // Widgets
    private Button button;
    private TextView textView;
    private ProgressBar spinner;

    private Button btnTest;

    // Der Bluetooth Service
    private BluetoothService mBtService = null;

    // Der Bluetooth Adapter
    private BluetoothAdapter mBTAdapter = null;

    // Buffer zur Datenuebermittlung
    private StringBuffer mOutStringBuffer;

    // gibt aktive Verbindung an
    private boolean activeConnection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Initialisiere Widgets
        initWidgets();

        // Adapter fuer die App holen
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        // Meldung falls das Ger?t kein Bluetooth unterst?tzt
        if (mBTAdapter == null) {
            Toast.makeText(this, getResources().getText(R.string.bt_not_available), Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Bluetooth aktivieren lassen, falls nicht aktiviert
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else if (mBtService == null) {
            mBtService = new BluetoothService(this, mHandler, mBTAdapter);

            // Initialize the buffer for outgoing messages
            mOutStringBuffer = new StringBuffer("");
        }

        // Sichtbarkeit ueberpruefen
        ensureDiscoverable();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBtService != null) {
            mBtService.stop();
            mBtService = null;
        }
        if (mBTAdapter != null) {
            mBTAdapter = null;
        }
        if (mOutStringBuffer != null) {
            mOutStringBuffer = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mBtService != null) {
            mBtService.stop();
            mBtService = null;
        }
        if (mBTAdapter != null) {
            mBTAdapter = null;
        }
        if (mOutStringBuffer != null) {
            mOutStringBuffer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mBtService != null) {
//            mBtService.stop();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // das hier muss implementiert werden, denn wenn bei onStart BT nicht aktiviert war,
        // wird das hier nach Aktivierung von BT aufgerufen.
        if (mBtService != null) {
            // Aber nur wenn der Status NONE ist, wissen wir das wir nicht schon gestartet haben
            if (mBtService.getState() == BluetoothService.STATE_NONE) {
                Log.d(TAG, "startConnection() from onResume()");
                mBtService.startConnection();
            }
        }
    }

    /*
    Stellt die Verbindung zum ausgewaehlten Geraet her
     */
    private void connectDevice(Intent data, boolean secure) {
        // MAC Addresse holen
        String address = data.getExtras()
                .getString(BluetoothSearchActivity.EXTRA_DEVICE_ADDRESS);
        // BluetoothDevice Objekt holen
        BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
        // Verbindung mit dem Geraet herstellen
        mBtService.connect(device, secure);
    }

    /*
    TODO bei uns wird das hier die Uebermittlung der Position an den anderen und die Beere vom ersten player
     */
    private void sendMessage(String message) {
        // Check that we're actually manageConnection before trying anything
        if (mBtService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {

            String testMsg = mBtService.getMyName() + " sagt Hallo!";

            // Get the message bytes and tell the BluetoothChatService to write
//            byte[] send = message.getBytes();
            byte[] send = testMsg.getBytes();
            mBtService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

    /*
   Wird immer aufgerufen, wenn aus anderen Activities ein Ergebnis zurueck kommt
    */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // Verbinden mit sicherem Kanal
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                else {
                    Toast.makeText(this, getString(R.string.no_device_selected),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // Verbinden mit unsicherem Kanal
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                } else {
                    Toast.makeText(this, getString(R.string.no_device_selected),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_DISCOVERABLE:
                // Antwort aus sichtbar werden
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // dann mach weiter
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this, getString(R.string.not_discoverable),
                                Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_ENABLE_BT:
                // Antwort aus BT aktivieren
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth-Suche beginnen
                    Intent serverIntent = new Intent(this, BluetoothSearchActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, getString(R.string.error_leave_app),
                            Toast.LENGTH_SHORT).show();
                    this.finish();
                }
        }
    }

    /*
    Handler zum Nachrichtenversand an das UI
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // Wenn sich der BT Verbindungsstatus aendert
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            spinner.setVisibility(View.GONE);
                            btnTest.setEnabled(true);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus("");
                            spinner.setVisibility(View.VISIBLE);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus("nicht verbunden");
                            break;
                    }
                    break;
//                case Constants.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
//
//                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // hier testweise erst mal nur eine Toast Nachricht
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(LobbyActivity.this, readMessage, Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // Wenn der Name des anderen Geraetes kommt
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(LobbyActivity.this, getString(
                            R.string.title_connected_to, mConnectedDeviceName),
                            Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    // Wenn eine Toast Nachricht kommt
                    Toast.makeText(LobbyActivity.this, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /*
    initialisiert die Widgets dieser Activity
     */
    private void initWidgets() {

        // Button
        button = (Button) findViewById(R.id.btn_lobby);
        button.setText(R.string.start_search);

        // TextView
        textView = (TextView) findViewById(R.id.text_view_lobby);
        setStatus(R.string.no_dude_selected);

        // Spinner
        spinner = (ProgressBar) findViewById(R.id.spinner_bar);
        spinner.setVisibility(View.GONE);

        // Test button
        btnTest = (Button) findViewById(R.id.test_button);
        btnTest.setEnabled(false);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Test");
            }
        });

        // ButtonListener setzten
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activeConnection) {
                    startFindDevices();
                } else {
                    startGame();
                }
            }
        });
    }

    /*
    BluetoothSearchActivity starten
     */
    private void startFindDevices() {
        // Bluetooth-Suche beginnen
        Intent serverIntent = new Intent(this, BluetoothSearchActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);//TODO Verbindungsmethode sollte anwaehlbar sein
    }

    /*
    Multiplayer starten
     */
    private void startGame() {
       //TODO!!!!!!!!!!!!!
    }

    /*
   Gibt eine Meldung, falls das eigene Geraet nicht sichtbar ist
    */
    private void ensureDiscoverable() {
        if (mBTAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 180);
            startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);
        }
    }

    /*
    Setzt den aktuellen Status in die TextView
    Wenn der Text Hardcoded ist
     */
    private void setStatus(CharSequence status) {
        textView.setText(status);
    }

    /*
    Setzt den aktuellen Status in die TextView
    Holt den Text aus den Resources
     */
    private void setStatus(int resId) {
        textView.setText(resId);
    }


}
