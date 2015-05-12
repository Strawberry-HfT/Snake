package de.hft.stuttgart.strawberry.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import de.hft.stuttgart.strawberry.fragments.DifficultyFragement;
import de.hft.stuttgart.strawberry.snake.R;

/**
 * Main activity der Snake-Applikation
 */
public class MainActivity extends ActionBarActivity {

    // TAG für den Logger
    private static final String TAG = MainActivity.class.getSimpleName();

    //Buttons
    private Button singleplayer;
    private Button multiplayer;
    private Button exit;

    //Animationen
    private Animation animScale;

    // Variablen
    private int geschwindigkeit;

    /*
    Methode wird beim Start der Applikation aufgerufen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Gibt das Bundle in die Superklasse
        super.onCreate(savedInstanceState);

        // Setzt die Rotation des Bildschims
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Verbindung mit .xml-file
        this.setContentView(R.layout.activity_main);

        // Initialisiert die Widgets der Activity
        this.initWidgets();

        // Initialisiert die Handler der Widgets
        this.initWidgetHandlers();
    }

    /*
    Beim Erstellen der OptionBar
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    Trigger der gewählten Option
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    initialisiert Widgets
     */
    private void initWidgets(){
        singleplayer = (Button) findViewById(R.id.singleplayer);
        multiplayer = (Button) findViewById(R.id.multiplayer);
        exit = (Button) findViewById(R.id.exit);
        animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
    }

    /*
    initialisiert Widget-Handler
     */
    private void initWidgetHandlers(){

        /*
        Klicklistener für Singleplayer Button, öffnet ein Fragment in dem die Schwierigkeit ausgewählt wird.
         */
        singleplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Startet die Button Animation
                v.startAnimation(animScale);

                //Timer verzögert das Ausführen der Anschlussaktion
                Timer myTimer = new Timer();
                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Erzeugen des Fragments
                                DifficultyFragement difficultyFragement = new DifficultyFragement();

                                // Bundle zur Übergabe von Parametern
                                Bundle bundle = new Bundle();

                                // Parameterübergabe in das Fragment
                                difficultyFragement.setArguments(bundle);

                                // Lädt den Fragmentmanager der Activity
                                FragmentManager fragmentManager = MainActivity.this.getFragmentManager();

                                // Startet die Transaction des Fragments
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                // Zeigt das Fragment an
                                difficultyFragement.show(fragmentTransaction, "test");
                            }
                        });
                    }
                }, 300);
            }
        });

        // Klicklistener für Multiplayer Button
        multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Startet die Button Animation
                v.startAnimation(animScale);

                //Timer verzögert das Ausführen der Anschlussaktion
                Timer myTimer = new Timer();
                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }, 300);
            }
        });

        // Klicklistener für Exit Button
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Startet die Button Animation
                v.startAnimation(animScale);

                //Timer verzögert das Ausführen der Anschlussaktion
                Timer myTimer = new Timer();
                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.finish();
                            }
                        });
                    }
                }, 300);
            }
        });
    }

    public void onLevelSelected(int level) {
        geschwindigkeit = level;
        Intent intent = new Intent(this, GPSingleActivity.class);
        intent.putExtra("difficulty", level);
        startActivity(intent);
    }

    // Getter und Setter
    public Button getMultiplayer() {
        return multiplayer;
    }

    public void setMultiplayer(Button multiplayer) {
        this.multiplayer = multiplayer;
    }

    public Button getSingleplayer() {
        return singleplayer;
    }

    public void setSingleplayer(Button singleplayer) {
        this.singleplayer = singleplayer;
    }

    public Button getExit() {
        return exit;
    }

    public void setExit(Button exit) {
        this.exit = exit;
    }

    public int getGeschwindigkeit() {
        return geschwindigkeit;
    }

    public void setGeschwindigkeit(int geschwindigkeit) {
        this.geschwindigkeit = geschwindigkeit;
    }
}
