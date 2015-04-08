package de.hft.stuttgart.strawberry.snake;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Von hier aus wird das Game-Play des
 * Singleplayermodus ausgefuehrt.
 * Created by Tommy_2 on 26.03.2015.
 */
public class GPSingleActivity extends Activity{

    // View in Activity
    private GPSingleView view;

    // Schlange
    private Snake snake;

    // Beere
    private Strawberry strawberry;

    // Display-Größe
    private Point displaySize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Point> startingPos = new ArrayList<Point>();

        // Ausrichtung Bildschirm (wird festgehalten)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Liest Displaygröße und speichert sie lokal
        getDisplaySize();

        // Schlange aus drei Gliedern erstellen
        for (int i = 0; i < 3; i++) {
            int xPos, yPos;
            // Startposition der Schlange
            Point startPos = new Point();
            xPos = displaySize.x / 3 - i * 50;
            yPos = displaySize.y / 3;
            startPos.set(xPos, yPos);
            startingPos.add(startPos);
        }

        // Initialisierung Variablen
        this.snake = new Snake(startingPos);
        this.strawberry = new Strawberry(displaySize);

        /*
         Initialisiert View
         Bekommt Activity, Schlange, Beere
          */
        this.view = new GPSingleView(this, snake, strawberry);

        // Übergabe Bitmap von View in Strawberry-Klasse
        this.strawberry.setBerryBitmap(this.view.getBerryBitmap());

        // Vollbildmodus der View
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        // Verknüpft die Activity mit der View
        this.setContentView(view);

        // startet Timer
        startTimer();
    }

    // Startet Timer
    private void startTimer(){
        // (Thread)Zeichnet die View immer wieder neu
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        snake.moveSnake();
                        view.invalidate();

                    }
                });
            }
        },10,100);
    }

    // Liest Displaygröße aus
    private void getDisplaySize(){
        Display display = getWindowManager().getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);
    }
}
