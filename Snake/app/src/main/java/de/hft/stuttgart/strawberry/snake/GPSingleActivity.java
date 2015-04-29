package de.hft.stuttgart.strawberry.snake;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Display;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Von hier aus wird das Game-Play des
 * Singleplayermodus ausgefuehrt.
 * Created by Tommy_2 on 26.03.2015.
 */
public class GPSingleActivity extends Activity implements SensorEventListener {

    // View in Activity
    private GPSingleView view;

    // Schlange
    private Snake snake;

    // Beere
    private Strawberry strawberry;

    // Display-Größe
    private Point displaySize;

    // Sensoren
    private Sensor sensorAccelorometer;
    private SensorManager sensorManager;

    // Variable für Bewegung
    private Movement direction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ausrichtung Bildschirm (wird festgehalten)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Liest Displaygröße und speichert sie lokal
        readDisplaySize();

        /*
         Initialisiert View
         Bekommt Activity, Schlange, Beere
          */
        this.view = new GPSingleView(this);

//        Übergabe Bitmap von View in Strawberry-Klasse
//        this.strawberry.setBerryBitmap(this.view.getBerryBitmap());

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
        this.strawberry = new Strawberry(displaySize);
        this.direction = new Movement();

        // startet Timer
        startTimer();

        // Sensor starten
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.sensorAccelorometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sensorManager.registerListener(this, sensorAccelorometer, SensorManager.SENSOR_DELAY_NORMAL);
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
                        snake.moveSnake(GPSingleActivity.this.direction);
                        view.invalidate();

                    }
                });
            }
        }, 0, 300);
    }

    // Liest Displaygröße aus
    private void readDisplaySize() {
        Display display = getWindowManager().getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);
    }

    // Sensoränderung (Richtung)
           @Override
        public void onSensorChanged(SensorEvent event) {

            //   System.out.println("X: "+event.values[0]+"\n"+"Y: "+event.values[1]+"\n");

        // Runter
        if (event.values[0] >= 0 && event.values[0] >= event.values[1] && event.values[0] >= (event.values[1]*-1)) {
            this.direction.setDown(true);
        }

        // Hoch
        if (event.values[0] <= 0 && event.values[0] <= event.values[1] && event.values[0] <= (event.values[1]*-1)) {
            this.direction.setUp(true);
        }

        // Rechts
        if (event.values[1] >= 0 && event.values[1] >= event.values[0] && event.values[1] >= (event.values[0]*-1)){
            this.direction.setRight(true);
        }

        // Links
        if (event.values[1] <= 0 && event.values[1] <= event.values[0] && event.values[1] <= (event.values[0]*-1)) {
            this.direction.setLeft(true);
        }
    }

    // Sensorgenauigkeit
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nichts
    }

    // Getter und Setter
    public void setDisplaySize(Point displaySize) {
        this.displaySize = displaySize;
    }

    public Point getDisplaySize() {
        return displaySize;
    }
}
