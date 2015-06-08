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

import de.hft.stuttgart.strawberry.common.Movement;
import de.hft.stuttgart.strawberry.controllers.SnakeGestureListener;
import de.hft.stuttgart.strawberry.controllers.SnakeSensorEventListener;
import de.hft.stuttgart.strawberry.snake.R;
import de.hft.stuttgart.strawberry.views.GPSingleSurfaceView;

/**
 * Von hier aus wird das Game-Play des
 * Singleplayermodus ausgefuehrt.
 */
public class GPSingleActivity extends Activity {

    // TAG für den Logger
    private static final String TAG = GPSingleActivity.class.getSimpleName();

    // Schwierigkeitsgrade
    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int HARD = 3;
    // Hält übergebene Schwierigkeit aus Fragment
    int difficulty;
    // gesetze Spielgeschwindigkeit
    private int selectedDifficulty;

    // View zur Activity
    private GPSingleSurfaceView testView;

    // Variable für Bewegung
    private Movement direction;

    // Sensoren
    private Sensor sensorAccelorometer;
    private SensorManager sensorManager;

    // Gestendetektor
    private GestureDetectorCompat gestureDetector;

    /*
    Lenkung der Schlange, wenn true dann Rotationssensor
    Initialwert = false
    */
    private boolean lenkungSensor = false;

    // Musik
    private boolean music = true;
    private MediaPlayer mediaPlayer;


    // Beim Erstellen der Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ausrichtung Bildschirm (wird festgehalten)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Schwierigkeitsgradabhängige Werte setzten
        this.initDifficulty();

        // Initialisiert View
        this.testView = new GPSingleSurfaceView(this);

        // Vollbildmodus der View, ab Android 4.4
        testView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Verknüpft die Activity mit der View
        this.setContentView(testView);

        // Initialisiert Variable zur Lenkung
        this.direction = new Movement();

        // Entscheidet ob Gestensteuerung oder Rotationssensoren
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
        this.testView.pause();
    }

    // Überschreiben aus Superklasse, zum starten des Spiels
    @Override
    protected void onResume() {
        super.onResume();
        if (music){
            // Musik
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        this.testView.resume();
    }

    // Getter
    public Movement getDirection() {
        return direction;
    }

    public int getSelectedDifficulty() {
        return selectedDifficulty;
    }


}
