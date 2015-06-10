package de.hft.stuttgart.strawberry.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hft.stuttgart.strawberry.bluetoothservice.BluetoothService;
import de.hft.stuttgart.strawberry.common.Constants;
import de.hft.stuttgart.strawberry.common.Movement;
import de.hft.stuttgart.strawberry.controllers.SnakeGestureListener;
import de.hft.stuttgart.strawberry.controllers.SnakeSensorEventListener;
import de.hft.stuttgart.strawberry.fragments.DifficultyFragment;
import de.hft.stuttgart.strawberry.snake.R;
import de.hft.stuttgart.strawberry.views.GPMultiSurfaceView;


/**
 * In dieser Activity wird die Verbindung zwischen zwei Geraeten
 * hergestellt. Fuer den Verbindungsaufbau wird der
 * {@link BluetoothService} benutzt und bei aktiver Verbindung
 * startet der Multiplayer Modus.
 */
public class GPMultiActivity extends Activity implements DialogInterface.OnDismissListener {

    // TAG fuer den Logger
    private static final String TAG = GPMultiActivity.class.getSimpleName();

    // Konstanten, für verschiedene Status
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    public static final int REQUEST_DISCOVERABLE = 4;

    // Name des verbundenen Geraetes
    private String mConnectedDeviceName = null;

    // Widgets
    private Button btnAction;
    private TextView textView;
    private ProgressBar spinner;

    // TODO ist voruebergehend noch drin, Levelauswahl kann aber auch in den obigen Button vershoben werden
    private Button btnLevel;

    // Der Bluetooth Service
    private BluetoothService mBtService = null;

    // Der Bluetooth Adapter
    private BluetoothAdapter mBTAdapter = null;

    // Buffer zur Datenuebermittlung
    private StringBuffer mOutStringBuffer;

    // gibt aktive Verbindung an
    private boolean activeConnection = false;

    // View in Activity
    private GPMultiSurfaceView multiView;

    // Start TimeStamp
    long startTime;

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

    // Wert fuer gewaehlen Schwierigkeitsgrad
    private boolean levelSelected = false;

    private int locator = 2;

    // empfangene Position als byte[]
    private byte[] recievedPosByteArray = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // falls noch nicht verbunden wurde, die Lobby-Sicht anzeigen
        if (!isActiveConnection()) {
//            LayoutInflater inflFactory = LayoutInflater.from(this);
//            lobbyView = inflFactory.inflate(R.layout.view_lobby, null);
//            lobbyView.setVisibility(View.VISIBLE);
            setContentView(R.layout.view_lobby);

            // Initialisiere Widgets
            initLobbyWidgets();

            // Adapter fuer die App holen
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();

            // Meldung falls das Ger?t kein Bluetooth unterst?tzt
            if (mBTAdapter == null) {
                Toast.makeText(this, getResources().getText(R.string.bt_not_available), Toast.LENGTH_LONG).show();
                this.finish();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Ablauf wenn die Lobby aktiv ist
        if (!isActiveConnection()) {

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
//                ensureDiscoverable();//TODO zu testzwecken auskommentiert
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "GPMulti onDestroy()");
        if (mBtService != null) {
            Log.d(TAG, "stopping Threads from onDestroy()");
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
            Log.d(TAG, "stopping Threads from onBackPressed()");
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
        Log.d(TAG, "onPause()");
        btnAction.setText(getString(R.string.start_search));
//        if (mBtService != null) {
//            mBtService.stop();
//        }
//        if (isRunningGame()) {
//            myTimer.cancel();
//            singleView.setVisibility(View.INVISIBLE);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
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
    Wird aufgerufen, wenn das DifficultyFragment wieder geschlossen wird
     */
    @Override
    public void onDismiss(DialogInterface dialog) {

        // Wenn der Schwierigkeitsgrad gewaehlt wurde, wird eine Meldung an den 2. Spieler versendet
        if (isLevelSelected() && isFirstPlayer()) {
            notifyDudeLevelSelected();
        }

        // Der Schwierigkeitsgrad wird für beide Spieler angezeigt
        StringBuffer lvlMsg = new StringBuffer();
        lvlMsg.append(getString(R.string.difficulty));
        switch (levelSpeed) {
            case Constants.SPEED_EASY:
                lvlMsg.append(getString(R.string.easy));
                break;
            case Constants.SPEED_MEDIUM:
                lvlMsg.append(getString(R.string.medium));
                break;
            case Constants.SPEED_HARD:
                lvlMsg.append(getString(R.string.hard));
                break;
        }

        // fuer Spieler 1
        Toast.makeText(this, lvlMsg, Toast.LENGTH_SHORT).show();

        Log.d(TAG, "selected: " + lvlMsg);

        // Buffer zuruecksetzen
        lvlMsg = null;
    }

    // Überschreiben aus Superklasse, zum Registrieren der Gesten
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    /*
    Multiplayer starten
     */
    private void startGame(long startTime) {

        this.initMusicSpeedByLevel();

        this.multiView = new GPMultiSurfaceView(this, startTime);

        // Vollbildmodus der View, ab Android 4.4
        multiView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Verknüpft die Activity mit der View
        this.setContentView(multiView);

        // Ablauf wenn das das Spiel gestartet wird
        // beim ersten Spieler wird auch die Erdbeere verwaltet
        if (isFirstPlayer()) {
            // Initialisierung Variablen (Schlange, Beere)
//            this.snake = new Snake(INIT_SNAKE_LENGTH, this.singleView.getmTileGrid());
//            this.strawberry = new Strawberry(this.singleView.getmTileGrid());
            this.direction = new Movement();
            this.direction.setRight(true);

            // startet Timer
//            startTimer();

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
        } else {
            this.direction = new Movement();
            this.direction.setLeft(true);
            // startet Timer
//            startTimer();

            if(lenkungSensor) {
                // Sensor starten
                this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                this.sensorAccelorometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                this.sensorManager.registerListener(new SnakeSensorEventListener(this.direction), sensorAccelorometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
            if(!lenkungSensor) {
                // Gestensensor, registriert die Klasse als Context und den ausgelagerten Listener
                this.gestureDetector = new GestureDetectorCompat(this, new SnakeGestureListener(this.direction));
            }

            if (music){
                // Musik
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        }
    }

    /*
    Initialisiert die Musikgeschwindigkeit
     */
    private void initMusicSpeedByLevel() {
        if (music) {
            mediaPlayer = new MediaPlayer();
        }

        switch (levelSpeed) {
            case Constants.SPEED_EASY:
                if (mediaPlayer != null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.audioeasy);
                }
                break;
            case Constants.SPEED_MEDIUM:
                if (mediaPlayer != null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.audiomedium);
                }
                break;
            case Constants.SPEED_HARD:
                if (mediaPlayer != null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.audiohard);
                }
                break;
            default:
                if (mediaPlayer != null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.audiomedium);
                }
                break;
        }
    }


    /*
    Versendet die aktuelle Position der Schlange
    TODO Beere fehlt noch
     */
    public synchronized void sendPosition() {
        Log.d(TAG, "prepare sendPosition()");

        // Pruefung, ob eine aktive Verbindung besteht
        if (mBtService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
        }

            // aktuelle Position der Schlange
            // TODO schauen ob die Position immernoch geholt wird
            ArrayList<Point> positions = multiView.getSnake().getPosition();

//            StringBuffer buffer = new StringBuffer();

            // Positionen als bytes initialisieren
            byte[] bytePositions = null;

            // index der bytelaenge, wird dem dude mitgeschickt
            int byteLength = positions.size() * 2;

            // Streams zum konvertieren
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dOut = new DataOutputStream(baos);

            // Positionen als bytes schreiben
            try {
                // zuerst den index damit man weiss wie viel bytes relevant sind
                dOut.writeByte(byteLength);
                // dann Positionen umschreiben
                for (Point point : positions) {
                    dOut.writeByte(point.x);
                    dOut.writeByte(point.y);
                }
            } catch (IOException e) {
                Log.e(TAG, "could not convert positions to bytes", e);
            }

            // aus dem Stream in das byte[] uebergeben
            bytePositions = baos.toByteArray();

            // TODO string variante die Position zu verschicken..
//            for (Point point : position) {
//                buffer.append(String.valueOf(point.x));
//                buffer.append(",");
//                buffer.append(String.valueOf(point.y));
//                buffer.append(";");
//            }
//            String tmp = buffer.toString();
//            bytePosition = tmp.getBytes();

            // an den dude senden
            Log.d(TAG, "sending positions");
            mBtService.write(bytePositions);
//            buffer.setLength(0);
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

    public void sendNotification(int id, String message) {

        // Sagt zu welcher Constanten es gesendet und empfangen werden soll
        this.locator = id;

        // Pruefung, ob eine aktive Verbindung besteht
        if (mBtService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        // Benachrichtigung senden
        if (message.length() > 0 && id !=0) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(id+";");
            stringBuffer.append(message);

            String fertig = stringBuffer.toString();
            byte[]byteFertig = fertig.getBytes();
            mBtService.write(byteFertig);

            // string buffer zuruecksetzen
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
                    String msg = getString(R.string.bt_turned_on);
                   Toast.makeText(GPMultiActivity.this, msg, Toast.LENGTH_SHORT).show();
                    this.onStart();
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
    Handler der Benachrichtigungen an das UI sendet und die Positionen empfaengt
    Erhaelt Nachrichten (Empfangen)
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
                            setActiveConnection(true);
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

                // wenn eine Benachrichtigung kommt
                case Constants.MESSAGE_READ:

                    // byte[] to string
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    String[] split = readMessage.split(";");

                    int locator = Integer.valueOf(split[0]);
                    String wert = split[1];

                    // hier verarbeitet der dude die Benachrichtigungen
                        switch (locator){
                            case Constants.LEVEL_SPEED:
                                if (wert.equals(String.valueOf(Constants.SPEED_EASY))) {
                                    levelSpeed = Constants.SPEED_EASY;
                                    setLevelSelected(true);
                                    Toast.makeText(GPMultiActivity.this, "leicht", Toast.LENGTH_SHORT).show();
                                } else if (wert.equals(String.valueOf(Constants.SPEED_MEDIUM))) {
                                    levelSpeed = Constants.SPEED_MEDIUM;
                                    setLevelSelected(true);
                                    Toast.makeText(GPMultiActivity.this, "mittel", Toast.LENGTH_SHORT).show();
                                } else if (wert.equals(String.valueOf(Constants.SPEED_HARD))) {
                                    levelSpeed = Constants.SPEED_HARD;
                                    setLevelSelected(true);
                                    Toast.makeText(GPMultiActivity.this, "schwer", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case Constants.NOTIFIER_SELECTED:
                                setLevelSelected(true);
                                break;
                            case Constants.NOTIFIER_START_GAME:
                                startGame(Long.valueOf(wert));
                                break;

                            // 2 PLAYER
                            case Constants.NOTIFIER_FIRST_BERRY:
                                String[] split1 = wert.split(":");
                                Point point = new Point(Integer.parseInt(split1[0]),Integer.parseInt(split1[1]));
                                // Sendet die Position der ersten Beere
                                multiView.getStrawberry().setBerryPosition(point);
                                // Zeichnet die erste Beere beim 2. Spieler
                                multiView.getStrawberry().drawBerry();
                                // Firstcall Ändern
                                multiView.setFirstCall(false);
                                Toast.makeText(GPMultiActivity.this,"Senden der ersten Beere angekommen",Toast.LENGTH_SHORT).show();
                                break;

                            // BEIDE
                            case Constants.NOTIFIER_BERRY_HIT:
                                String[] split2 = wert.split(":");
                                Point point2 = new Point(Integer.parseInt(split2[0]),Integer.parseInt(split2[1]));
                                multiView.getStrawberry().setBerryPosition(point2);
                                Toast.makeText(GPMultiActivity.this, "Beere getroffen", Toast.LENGTH_SHORT).show();

                    }
                    break;
                // Wenn der Name des anderen Geraetes kommt
                case Constants.MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(GPMultiActivity.this, getString(
                            R.string.title_connected_to, mConnectedDeviceName),
                            Toast.LENGTH_SHORT).show();
                    break;
                // Wenn eine Toast Nachricht kommt
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(GPMultiActivity.this, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;

                // TODO Ich teste hier, ob die Postion der anderen Schlange ankommt
                case Constants.POSITION_READ:

                    // Streams
                    ByteArrayInputStream bais = new ByteArrayInputStream(recievedPosByteArray);
                    DataInputStream dIn = new DataInputStream(bais);

                    // empfangene Position des dudes
                    ArrayList<Point> dudesPositions = new ArrayList<>();
                    Point position = null;
                    int x = 0;
                    int y = 0;

                    // laenge der Bytes die nicht genutzt werden
                    int unusedBytes;

                    boolean isX = true;
                    boolean gotBoth = false;

                    try {
                        // Indexgroesse die beim senden vom anderen gesetzt wird, damit wir wissen
                        // wie viele bytes vom Array fuer diesen Durchgang relevant sind.
                        int byteIndex = dIn.readByte();

                        if (byteIndex > 0) {
                            unusedBytes = Constants.STREAM_BUFFER_SIZE - byteIndex - 1;
                        } else {
                            unusedBytes = Constants.STREAM_BUFFER_SIZE;
                        }

                        // den Stream solange lesen, bis die schlange ausgelesen ist
                        // (der buffer ist zur Zeit immer 1024, wird aber nicht voll ausgenutzt)
                        while (dIn.available() > unusedBytes) {
                            // aktueller Wert
                            int currentByte = dIn.readByte();

                            // damit werden die leeren nullen nicht mit aufgelistet
//                            if (currentByte == 0) {
//                                continue;
//                            }

                            // X, Y Positionen setzten
                            if (isX) {
                                x = currentByte;
                                isX = false;
                            } else {
                                y = currentByte;
                                isX = true;
                                gotBoth = true;
                            }

                            // Point aus X, Y erstellen
                            if (gotBoth) {
                                position = new Point(x, y);
                                dudesPositions.add(position);
                                gotBoth = false;
                            }
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "could not read sent position");
                    }

                    // TODO testweise gebe ich die Position als Toast aus
                    if (dudesPositions.size() > 0) {
                        StringBuilder positionListString = new StringBuilder();
                        int i = 0;

                        for (Point p : dudesPositions) {
                            i++;
                            if (i == dudesPositions.size()) {
                                positionListString.append("Position " + i + "->  x:" + p.x + " y: " +p.y);
                            } else {
                                positionListString.append("Position " + i + "->  x:" + p.x + " y: " +p.y + "\n");
                            }
                        }
                        Toast.makeText(GPMultiActivity.this, positionListString, Toast.LENGTH_LONG).show();
                    }
                    break;
                case Constants.LEVEL_SPEED:

            }
        }
    };

    /*
    initialisiert die Widgets dieser Activity
     */
    private void initLobbyWidgets() {

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
                    if(isFirstPlayer()) {

                        // Aktuelle Zeit + 5 Sekunden
//                        startTime = System.currentTimeMillis()+5000;
                        notifyDudeGameStarted(5000);
                        startGame(startTime);
                    }
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
    Schluesselnachricht, dass Spieler 1 eine Schwierigkeit gewaehlt hat
     */
    private void notifyDudeLevelSelected() {
        Log.d(TAG, "notifyDudeLevelSelected()");
        String speed = String.valueOf(levelSpeed);
        if (isFirstPlayer()) {
            sendNotification(Constants.LEVEL_SPEED, speed);
        }
    }

    private void notifyDudeGameStarted(long startTime) {
        Log.d(TAG, "notifyDudeGameStarted()");
        if (isFirstPlayer()) {
            sendNotification(Constants.NOTIFIER_START_GAME, String.valueOf(startTime));
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
        difficultyFragment.show(fragmentTransaction, Constants.FRAGMENT_TAG);
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

    /*
    Getter und Setter fuer globale Variablen der Activity
     */
    public Handler getmHandler() {
        return mHandler;
    }

    public boolean isLevelSelected() {
        return levelSelected;
    }

    // Ändert den Buttontext
    public void setLevelSelected(boolean levelSelected) {
        this.levelSelected = levelSelected;

        if (levelSelected) {
            btnAction.setText(R.string.lets_go);
        } else {
            btnAction.setText(R.string.start_search);
        }
    }

    public boolean isFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(boolean firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public boolean isActiveConnection() {
        return activeConnection;
    }

    public void setActiveConnection(boolean activeConnection) {
        this.activeConnection = activeConnection;
    }

    public synchronized byte[] getRecievedPosByteArray() {
        return recievedPosByteArray;
    }

    public synchronized void setRecievedPosByteArray(byte[] recievedPosByteArray) {
        this.recievedPosByteArray = recievedPosByteArray;
    }

    public void setLevelSpeed(int levelSpeed) {
        this.levelSpeed = levelSpeed;
    }

    public Button getBtnAction() {
        return btnAction;
    }

    public int getLevelSpeed() {
        return levelSpeed;
    }

    public Movement getDirection() {
        return direction;
    }

    public int getLocator() {
        return locator;
    }
}
