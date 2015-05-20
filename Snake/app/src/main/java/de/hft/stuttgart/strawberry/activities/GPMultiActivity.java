package de.hft.stuttgart.strawberry.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import de.hft.stuttgart.strawberry.blutetoothservice.BluetoothService;
import de.hft.stuttgart.strawberry.common.Constants;
import de.hft.stuttgart.strawberry.snake.R;

/**
 * Created by Tommy_2 on 20.05.2015.
 */
public class GPMultiActivity extends Activity {

    // TAG für den Logger
    private static final String TAG = GPMultiActivity.class.getSimpleName();

    // Anfragecodes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Name des verbundenen Gerätes
    private String mConnectedDeviceName = null;

    // Der Bluetooth Service
    private BluetoothService mBtService = null;

    private StringBuffer mOutStringBuffer;

    private BluetoothAdapter mBluetoothAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Adapter für die App holen
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Meldung falls das Gerät kein Bluetooth unterstützt
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, getResources().getText(R.string.bt_not_available), Toast.LENGTH_LONG).show();
            this.finish();
        }

        //TODO wir brauchen noch eine View die vor dem Gameplay aufgerufen wird und im Hintergrund angezeigt wird
    }

    @Override
    public void onStart() {
        super.onStart();

        // Bluetooth aktivieren lassen, falls nicht aktiviert
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else if (mBtService == null) {
            // Initialize the BluetoothChatService to perform bluetooth connections
            mBtService = new BluetoothService(this, mHandler);

            // Initialize the buffer for outgoing messages
            mOutStringBuffer = new StringBuffer("");

            //TODO hier wenn nötig etwas vorbereiten wie Schlange initialisieren usw, dann die bluetooth suchgeschichte starten.
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBtService != null) {
            mBtService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // das hier muss implementiert werden, denn wenn bei onStart BT nicht aktiviert war,
        // wird das hier nach Aktivierung von BT aufgerufen.
        if (mBtService != null) {
            // Aber nur wenn der Status NONE ist, wissen wir das wir nicht schon gestartet haben
            if (mBtService.getState() == BluetoothService.STATE_NONE) {
                mBtService.startConnection();
            }
        }
    }

    /*
    Stellt die Verbindung zum ausgewählten Gerät her
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(BluetoothSearchActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBtService.connect(device, secure);
    }

    /*
    TODO bei uns wird das hier die Übermittlung der Position an den anderen und die Beere vom ersten player
    TODO muss noch umgeschrieben werden
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBtService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "Nicht verbunden..", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mBtService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
        }
    }

    /*
   Vorbereiten zur Verbindung mit gewähltem Gerät
    */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
//                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
//                    Toast.makeText(getActivity(), "Fehler bei der Verbindung, verlasse App",
//                            Toast.LENGTH_SHORT).show();
//                    getActivity().finish();
                }
        }
    }

    /*
    Handler zum verschicken der Nachrichten
    TODO Muss dann zu den Positionen modifiziert werden, ist jetzt noch nicht lauffähig, hier sind auch noch überflüssige sachen aus dem Beispiel
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
//                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));

                            break;
                        case BluetoothService.STATE_CONNECTING:
//                            setStatus("Verbinde..");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
//                            setStatus("nicht verbunden");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                    if (null != activity) {
//                        Toast.makeText(activity, "Connected to "
//                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    }
                    break;
                case Constants.MESSAGE_TOAST:
//                    if (null != activity) {
//                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
//                                Toast.LENGTH_SHORT).show();
//                    }
                    break;
            }
        }
    };

    /*
    Überprüft, ob das eigene Gerät sichtbar für andere ist
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
}
