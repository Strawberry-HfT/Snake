//package de.hft.stuttgart.strawberry.fragments;
//
//import android.app.ActionBar;
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.EditorInfo;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import de.hft.stuttgart.strawberry.activities.BluetoothSearchActivity;
//import de.hft.stuttgart.strawberry.blutetoothservice.BluetoothService;
//import de.hft.stuttgart.strawberry.common.Constants;
//import de.hft.stuttgart.strawberry.snake.R;
//
///**
// * DIESE KLASSE NICHT BEACHTEN!!
// * Die hab ich nur reingemacht weil ich den Chat testen wollte
// */
//public class GPMultiFragment extends Fragment {
//
//    // TAG f�r den Logger
//    private static final String TAG = GPMultiFragment.class.getSimpleName();
//
//    // Anfragecodes
//    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
//    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
//    private static final int REQUEST_ENABLE_BT = 3;
//
//    // Name des verbundenen Ger�tes
//    private String mConnectedDeviceName = null;
//
//    // Der Bluetooth Service
//    private BluetoothService mBtService = null;
//
//    /*
//    Die folgenden Objekte kommen nur um die Verbindung mal zu testen
//     */
//    // Layout Views
//    private ListView mConversationView;
//    private EditText mOutEditText;
//    private Button mSendButton;
//
//    private ArrayAdapter<String> mConversationArrayAdapter;
//    private StringBuffer mOutStringBuffer;
//
//    private BluetoothAdapter mBluetoothAdapter = null;
//
//    /*
//    OnCreate Methode
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        //TODO haben wir nur testweise hier
//        setHasOptionsMenu(true);
//
//        // Adapter f�r die App holen
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        // Meldung falls das Ger�t kein Bluetooth unterst�tzt
//        if (mBluetoothAdapter == null) {
//            FragmentActivity fragmentActivity = getActivity();
//            Toast.makeText(fragmentActivity, getResources().getText(R.string.bt_not_available), Toast.LENGTH_LONG).show();
//            fragmentActivity.finish();
//        }
//
//    }
//
//    /*
//    TODO nur testweise so implementiert
//     */
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // Bluetooth aktivieren lassen, falls nicht aktiviert
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        } else if (mBtService == null) {
//            setupChat();
//        }
//    }
//
//    /*
//    TODO nur testweise so implementiert
//     */
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mBtService != null) {
//            mBtService.stop();
//        }
//    }
//
//    /*
//    TODO nur testweise so implementiert
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        // das hier muss implementiert werden, denn wenn bei onStart BT nicht aktiviert war,
//        // wird das hier nach Aktivierung von BT aufgerufen.
//        if (mBtService != null) {
//            // Aber nur wenn der Status NONE ist, wissen wir das wir nicht schon gestartet haben
//            if (mBtService.getState() == BluetoothService.STATE_NONE) {
//                mBtService.startConnection();
//            }
//        }
//    }
//
//    /*
//    TODO nur zu testzwecken hier drin
//     */
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.activity_test_multiplayer, container, false);
//    }
//
//    /*
//    TODO nur zu testzwecken hier drin
//     */
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        mConversationView = (ListView) view.findViewById(R.id.in);
//        mOutEditText = (EditText) view.findViewById(R.id.edit_text_out);
//        mSendButton = (Button) view.findViewById(R.id.button_send);
//    }
//
//
//
//    /*
//    Vorbereiten zur Verbindung mit gew�hltem Ger�t
//     */
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CONNECT_DEVICE_SECURE:
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, true);
//                }
//                break;
//            case REQUEST_CONNECT_DEVICE_INSECURE:
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, false);
//                }
//                break;
//            case REQUEST_ENABLE_BT:
//                // When the request to enable Bluetooth returns
//                if (resultCode == Activity.RESULT_OK) {
//                    // Bluetooth is now enabled, so set up a chat session
//                    setupChat();
//                } else {
//                    // User did not enable Bluetooth or an error occurred
//                    Log.d(TAG, "BT not enabled");
//                    Toast.makeText(getActivity(), "Fehler bei der Verbindung, verlasse App",
//                            Toast.LENGTH_SHORT).show();
//                    getActivity().finish();
//                }
//        }
//    }
//
//    /*
//    TODO bei uns wird das die Initialisierung der View sein, wo welche Schlange ist und wo die Beere ist
//     */
//    private void setupChat() {
//        Log.d(TAG, "setupChat()");
//
//        // Initialize the array adapter for the conversation thread
//        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message_test);
//
//        mConversationView.setAdapter(mConversationArrayAdapter);
//
//        // Initialize the compose field with a listener for the return key
//        mOutEditText.setOnEditorActionListener(mWriteListener);
//
//        // Initialize the send button with a listener that for click events
//        mSendButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Send a message using content of the edit text widget
//                View view = getView();
//                if (view != null) {
//                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
//                    String message = textView.getText().toString();
//                    sendMessage(message);
//                }
//            }
//        });
//
//        // Initialize the BluetoothChatService to perform bluetooth connections
//        mBtService = new BluetoothService(getActivity(), mHandler);
//
//        // Initialize the buffer for outgoing messages
//        mOutStringBuffer = new StringBuffer("");
//    }
//
//    /*
//    TODO bei uns wird das hier die �bermittlung der Position an den anderen und die Beere vom ersten player
//     */
//    private void sendMessage(String message) {
//        // Check that we're actually connected before trying anything
//        if (mBtService.getState() != BluetoothService.STATE_CONNECTED) {
//            Toast.makeText(getActivity(), "Nicht verbunden..", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Check that there's actually something to send
//        if (message.length() > 0) {
//            // Get the message bytes and tell the BluetoothChatService to write
//            byte[] send = message.getBytes();
//            mBtService.write(send);
//
//            // Reset out string buffer to zero and clear the edit text field
//            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
//        }
//    }
//
//    /*
//    TODO Nur zu Testzwecken hier drin
//     */
//    private TextView.OnEditorActionListener mWriteListener
//            = new TextView.OnEditorActionListener() {
//        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//            // If the action is a key-up event on the return key, send the message
//            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
//                String message = view.getText().toString();
//                sendMessage(message);
//            }
//            return true;
//        }
//    };
//
//    /*
//    TODO Nur zu Testzwecken hier drin
//     */
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.bluetooth_chat, menu);
//    }
//
//    /*
//    TODO Nur zu Testzwecken hier drin
//     */
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.secure_connect_scan: {
//                // Launch the DeviceListActivity to see devices and do scan
//                Intent serverIntent = new Intent(getActivity(), BluetoothSearchActivity.class);
//                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
//                return true;
//            }
//            case R.id.insecure_connect_scan: {
//                // Launch the DeviceListActivity to see devices and do scan
//                Intent serverIntent = new Intent(getActivity(), BluetoothSearchActivity.class);
//                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
//                return true;
//            }
//            case R.id.discoverable: {
//                // Ensure this device is discoverable by others
//                ensureDiscoverable();
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /*
//    Handler zum verschicken der Nachrichten
//    //TODO Muss dann zu den Positionen modifiziert werden
//     */
//    private final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            FragmentActivity activity = getActivity();
//            switch (msg.what) {
//                case Constants.MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case BluetoothService.STATE_CONNECTED:
//                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                            mConversationArrayAdapter.clear();
//                            break;
//                        case BluetoothService.STATE_CONNECTING:
//                            setStatus("Verbinde..");
//                            break;
//                        case BluetoothService.STATE_LISTEN:
//                        case BluetoothService.STATE_NONE:
//                            setStatus("nicht verbunden");
//                            break;
//                    }
//                    break;
//                case Constants.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
//                    break;
//                case Constants.MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    // construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
//                    break;
//                case Constants.MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                    if (null != activity) {
//                        Toast.makeText(activity, "Connected to "
//                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//                case Constants.MESSAGE_TOAST:
//                    if (null != activity) {
//                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//            }
//        }
//    };
//
//    /*
//    �berpr�ft, ob das eigene Ger�t sichtbar f�r andere ist
//     */
//    private void ensureDiscoverable() {
//        if (mBluetoothAdapter.getScanMode() !=
//                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
//            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//            startActivity(discoverableIntent);
//        }
//    }
//
//    /*
//    Setzt den Status
//     */
//    private void setStatus(int resId) {
//        FragmentActivity activity = getActivity();
//        if (null == activity) {
//            return;
//        }
//        final ActionBar actionBar = activity.getActionBar();
//        if (null == actionBar) {
//            return;
//        }
//        actionBar.setSubtitle(resId);
//    }
//
//    /*
//    Setzt den Status
//     */
//    private void setStatus(CharSequence subTitle) {
//        FragmentActivity activity = getActivity();
//        if (null == activity) {
//            return;
//        }
//        final ActionBar actionBar = activity.getActionBar();
//        if (null == actionBar) {
//            return;
//        }
//        actionBar.setSubtitle(subTitle);
//    }
//
//}
