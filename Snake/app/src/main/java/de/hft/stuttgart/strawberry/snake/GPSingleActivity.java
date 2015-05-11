package de.hft.stuttgart.strawberry.snake;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Von hier aus wird das Game-Play des
 * Singleplayermodus ausgefuehrt.
 * Created by Tommy_2 on 26.03.2015.
 */
public class GPSingleActivity extends Activity {

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
    private boolean musik = true;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ausrichtung Bildschirm (wird festgehalten)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


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
        this.snake = new Snake(3, this.view.getmTileGrid());
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

        if (musik){
            // Musik
            mediaPlayer = new MediaPlayer();
            mediaPlayer = MediaPlayer.create(this, R.raw.background);
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
        }, 0, 300);
    }

    // Überschreiben aus Superklasse, zum Registrieren der Gesten
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }
}
