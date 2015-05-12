package de.hft.stuttgart.strawberry.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import de.hft.stuttgart.strawberry.views.GPSingleView;
import de.hft.stuttgart.strawberry.common.Movement;
import de.hft.stuttgart.strawberry.snake.R;
import de.hft.stuttgart.strawberry.snake.Snake;
import de.hft.stuttgart.strawberry.controllers.SnakeGestureListener;
import de.hft.stuttgart.strawberry.controllers.SnakeSensorEventListener;
import de.hft.stuttgart.strawberry.common.Strawberry;

/**
 * Von hier aus wird das Game-Play des
 * Singleplayermodus ausgefuehrt.
 * Created by Tommy_2 on 26.03.2015.
 */
public class GPSingleActivity extends Activity {

    // TAG für den Logger
    private static final String TAG = GPSingleActivity.class.getSimpleName();

    // Schwierigkeitsgrade
    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int HARD = 3;

    int difficulty;

    // Anfangslänge der Schlange
    private static final int INIT_SNAKE_LENGTH = 3;

    // View in Activity
    private GPSingleView view;

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
    private boolean music = true;
    private MediaPlayer mediaPlayer;

    // gesetze Spielgeschwindigkeit
    private int selectedDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ausrichtung Bildschirm (wird festgehalten)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Schwierigkeitsgradabhängige Werte setzten
        this.initDifficulty();

        // Initialisiert View
        this.view = new GPSingleView(this);

        // Vollbildmodus der View, ab Android 4.4
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Verknüpft die Activity mit der View
        this.setContentView(view);

        // Initialisierung Variablen (Schlange, Beere)
        this.snake = new Snake(INIT_SNAKE_LENGTH, this.view.getmTileGrid());
        this.strawberry = new Strawberry(this.view.getmTileGrid());
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
                        snake.moveSnake(GPSingleActivity.this.direction);
                        snake.checkCollisionBerry(strawberry);
                        if(snake.checkCollisionSnake()){
                            mediaPlayer.stop();
                            finish();
                        }
                        view.invalidate();
                    }
                });
            }
        }, 0, selectedDifficulty);
    }

    /*
    Initialisiert die Werte, die von der Schwierigkeitsauswahl abhängen
     */
    private void initDifficulty() {
        if (music) {
            mediaPlayer = new MediaPlayer();
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            difficulty = extras.getInt("difficulty");
            Log.d(TAG, "Selected Difficulty: " + difficulty);
        } else {
            difficulty = MEDIUM;
            Log.d(TAG, "No Difficulty Selected");
        }

        switch (difficulty) {
            case EASY:
                selectedDifficulty = 300;
                if (music) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.audioeasy);
                }
                break;
            case MEDIUM:
                selectedDifficulty = 180;
                if (music) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.audiomedium);
                }
                break;
            case HARD:
                selectedDifficulty = 80;
                if (music) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.audiohard);
                }
                break;
            default:
                selectedDifficulty = 300;
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

    // Überschreiben aus Superklasse, zum stoppen der Spielmusik
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    // Überschreiben aus Superklasse, zum stoppen der Spielmusik
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }
}
