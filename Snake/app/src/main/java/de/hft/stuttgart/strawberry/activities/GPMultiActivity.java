package de.hft.stuttgart.strawberry.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import de.hft.stuttgart.strawberry.bluetoothservice.BluetoothService;
import de.hft.stuttgart.strawberry.common.Constants;
import de.hft.stuttgart.strawberry.common.Movement;
import de.hft.stuttgart.strawberry.common.Strawberry;
import de.hft.stuttgart.strawberry.controllers.SnakeGestureListener;
import de.hft.stuttgart.strawberry.controllers.SnakeSensorEventListener;
import de.hft.stuttgart.strawberry.fragments.DifficultyFragment;
import de.hft.stuttgart.strawberry.snake.R;
import de.hft.stuttgart.strawberry.snake.Snake;
import de.hft.stuttgart.strawberry.views.GPSingleView;

/**
 * In dieser Activity wird die Verbindung zwischen zwei Geraeten
 * hergestellt. Fuer den Verbindungsaufbau wird der
 * {@link BluetoothService} benutzt und bei aktiver Verbindung
 * startet der Multiplayer Modus.
 */
public class GPMultiActivity extends Activity implements DialogInterface.OnDismissListener {

    // TAG fuer den Logger
    private static final String TAG = GPMultiActivity.class.getSimpleName();

    // Tag fuer Fragment
    private static final String FRAGMENT_TAG = "difficulty_fragment";

    // Konstanten, für verschiedene Status
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    public static final int REQUEST_DISCOVERABLE = 4;

    // Schwierigkeitsgrade
    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int HARD = 3;

    private static final String NOTIFIER_MESSAGE = "level_selected";

    int selectedDifficulty;

    // Name des verbundenen Geraetes
    private String mConnectedDeviceName = null;

    // Widgets
    private Button btnAction;
    private TextView textView;
    private ProgressBar spinner;

    private Button btnLevel;

    // Der Bluetooth Service
    private BluetoothService mBtService = null;

    // Der Bluetooth Adapter
    private BluetoothAdapter mBTAdapter = null;

    // Buffer zur Datenuebermittlung
    private StringBuffer mOutStringBuffer;

    // gibt aktive Verbindung an
    private boolean activeConnection = false;//TODO wird noch nicht gesetzt, also immer false bis jetzt


    // Anfangslänge der Schlange
    private static final int INIT_SNAKE_LENGTH = 3;

    // View in Activity
    private GPSingleView singleView;

    // Schlange
    private Snake snake;

    // Beere
    private Strawberry strawberry;

    // Variable für Bewegung
    private Movement direction;

    // Sensoren
    private Sensor sensorAccelorometer;
    private SensorManager sensorManager;

    // Gestendetektor
    private GestureDetectorCompat gestureDetector;

    // Lenkung der Schlange, wenn true dann Rotationssensor
    // Initialwert = false
    private boolean lenkungSensor = false;

    // Musik
    private boolean music = false;
    private MediaPlayer mediaPlayer;

    // gesetze Spielgeschwindigkeit
    private int levelSpeed;

    // gibt an, welcher Spieler der erste ist
    private boolean firstPlayer = false;

    // gibt an, ob ein Spieler geaehlt wurde
    private boolean deviceSelected = false;

    // Wert fuer Singleplayermodus
    private boolean fromSingleplayer = false;

    // Wert fuer gewaehlen Schwierigkeitsgrad
    private boolean levelSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // falls noch nicht verbunden wurde, die Lobby-Sicht anzeigen
        if (!activeConnection) {
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
        } else {
            this.singleView = new GPSingleView(this);

            // Vollbildmodus der View, ab Android 4.4
            singleView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            // Verknüpft die Activity mit der View
            this.setContentView(singleView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Ablauf wenn die Lobby aktiv ist
        if (!activeConnection) {

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
            if (!deviceSelected) {
                ensureDiscoverable();
            }
        } else {
            // Ablauf wenn das das Spiel gestartet wird
            // beim ersten Spieler wird auch die Erdbeere verwaltet
            if (firstPlayer) {
                // Initialisierung Variablen (Schlange, Beere)
                this.snake = new Snake(INIT_SNAKE_LENGTH, this.singleView.getmTileGrid());
                this.strawberry = new Strawberry(this.singleView.getmTileGrid());
                this.direction = new Movement();

                // startet Timer
                startTimer();

                if(lenkungSensor) {
                    // Sensor starten
                    this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                    this.sensorAccelorometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    this.sensorManager.registerListener(new SnakeSensorEventListener(this.direction), sensorAccelorometer, SensorManager.SENSOR_DELAY_NORMAL);
                }
                if(!lenkungSensor) {
                    // Gestensensor, registiert die Klasse als Context und den ausgelagerten Listener
                    this.gestureDetector = new GestureDetectorCompat(this, new SnakeGestureListener(this.direction));
                }

                if (music){
                    // Musik
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            } else { // TODO hier muss für den zweiten Player noch die view erstellt werden (glaub) und die schlange vom ersten player geholt werden
                // beim zweiten Spieler wird nur die eigene Schlange initialisiert
//                this.snake = new Snake(INIT_SNAKE_LENGTH, this.singleView.getmTileGrid());
//                this.direction = new Movement();
            }
        }
    }

    /*
    Initialisiert die Werte, die von der Schwierigkeitsauswahl abhängen
     */
    private void initSnakeSpeed() {
        if (music) {
            mediaPlayer = new MediaPlayer();
        }

        switch (selectedDifficulty) {
            case EASY:
                levelSpeed = 300;
                if (music) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.audioeasy);
                }
                break;
            case MEDIUM:
                levelSpeed = 180;
                if (music) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.audiomedium);
                }
                break;
            case HARD:
                levelSpeed = 80;
                if (music) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.audiohard);
                }
                break;
            default:
                levelSpeed = 300;
                if (music) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.audiomedium);
                }
                break;
        }
    }

    // Überschreiben aus Superklasse, zum Registrieren der Gesten
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
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

    @Override
    public void onDismiss(DialogInterface dialog) {

        String msg = "Level: " + levelSpeed;

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        sendMessage(msg);

    }

    // Startet Timer
    private void startTimer() {
        // (Thread)Zeichnet die View immer wieder neu
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        strawberry.drawBerry();
                        snake.moveSnake(GPMultiActivity.this.direction);
                        snake.checkCollisionBerry(strawberry);
                        if (snake.checkCollisionSnake()) {
                            mediaPlayer.stop();
                            finish();
                        }
                        singleView.invalidate();
                    }
                });
            }
        }, 0, levelSpeed);
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

            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
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
                    deviceSelected = true;
                    firstPlayer = true;
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
                    deviceSelected = true;
                    firstPlayer = true;
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
                            if (isFirstPlayer()) {
                                btnLevel.setEnabled(true);
                            }
                            if (isLevelSelected()) {
                                btnAction.setText(R.string.lets_go);
                                if (isFirstPlayer()) {
                                    // Wenn der Schwierigkeitsgrad gewaehlt wurde, wird eine Meldung an den 2. Spieler versendet
                                    notifyLevelSelected();
                                }
                            }
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

                    // Wenn der Schwierigkeitsgrad gewaehlt wurde, bekommt der 2. Spieler hier die Nachricht
                    if(!isFirstPlayer()) {
                        if(readMessage.equals(NOTIFIER_MESSAGE)) {
                            setLevelSelected(true);
                            btnAction.setText(R.string.lets_go);
                        }
                    }


                    Toast.makeText(GPMultiActivity.this, readMessage, Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // Wenn der Name des anderen Geraetes kommt
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(GPMultiActivity.this, getString(
                            R.string.title_connected_to, mConnectedDeviceName),
                            Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    // Wenn eine Toast Nachricht kommt
                    Toast.makeText(GPMultiActivity.this, msg.getData().getString(Constants.TOAST),
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
        btnAction = (Button) findViewById(R.id.btn_lobby);
        btnAction.setText(R.string.start_search);

        // TextView
        textView = (TextView) findViewById(R.id.text_view_lobby);
        setStatus(R.string.no_dude_selected);

        // Spinner
        spinner = (ProgressBar) findViewById(R.id.spinner_bar);
        spinner.setVisibility(View.GONE);

        // Level button
        btnLevel = (Button) findViewById(R.id.level_button);
        btnLevel.setEnabled(false);

        btnLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Schwierigkeitsgrad ausewaehlen
                showDifficultyFragment();
            }
        });

        // ButtonListener setzten
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!activeConnection) {
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

    private void notifyLevelSelected () {
        if (isFirstPlayer()) {
            sendMessage(NOTIFIER_MESSAGE);
        }
    }

    /*
    Zeigt das Fragment zum auswaehlen des Schwierigkeitsgrades
     */
    private void showDifficultyFragment() {
        // Erzeugen des Fragments
        DifficultyFragment difficultyFragment = new DifficultyFragment();

        // Bundle zur Übergabe von Parametern
        Bundle bundle = new Bundle();

        // Parameterübergabe in das Fragment
        difficultyFragment.setArguments(bundle);

        // Lädt den Fragmentmanager der Activity
        FragmentManager fragmentManager = GPMultiActivity.this.getFragmentManager();

        // Startet die Transaction des Fragments
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Zeigt das Fragment an
        difficultyFragment.show(fragmentTransaction, FRAGMENT_TAG);
    }

    /*
    Setzt den aktuellen Status in die TextView
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

    public int getLevelSpeed() {
        return levelSpeed;
    }

    public void setLevelSpeed(int levelSpeed) {
        this.levelSpeed = levelSpeed;
    }

    public boolean isLevelSelected() {
        return levelSelected;
    }

    public void setLevelSelected(boolean levelSelected) {
        this.levelSelected = levelSelected;
    }

    public boolean isFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(boolean firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public boolean isFromSingleplayer() {
        return fromSingleplayer;
    }
}
