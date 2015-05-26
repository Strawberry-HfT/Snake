package de.hft.stuttgart.strawberry.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import de.hft.stuttgart.strawberry.fragments.DifficultyFragment;
import de.hft.stuttgart.strawberry.snake.R;

/**
 * Main activity der Snake-Applikation
 */
public class MainActivity extends ActionBarActivity implements DialogInterface.OnDismissListener {

    // TAG für den Logger
    private static final String TAG = MainActivity.class.getSimpleName();

    // Tag fuer Fragment
    private static final String FRAGMENT_TAG = "difficulty_fragment";

    // Konstante zur Schwierigkeitsübergabe an das Spiel
    public static final String BUNDLE_DIFFICULTY = "difficulty";

    //Buttons
    private Button singleplayer;
    private Button multiplayer;
    private Button exit;

    //Animationen
    private Animation animScale;

    // Schwierigkeitsgrad
    private int selectedDifficulty;

    // Wert fuer Singleplayermodus
    private boolean fromSingleplayer;

    // Wert fuer gewaehlen Schwierigkeitsgrad
    private boolean levelSelected = false;

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

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialisiert die Handler der Widgets
        this.initWidgetHandlers();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        // Wenn der Schwierigkeitsgrad gewaehlt wurde, startet der Singleplayer
        if (isFromSingleplayer() && isLevelSelected()) {
            Intent intentSingle = null;
            intentSingle = new Intent(MainActivity.this, GPSingleActivity.class);
            intentSingle.putExtra(BUNDLE_DIFFICULTY, getSelectedDifficulty());
            startActivity(intentSingle);
        } else {
            Toast.makeText(this, "Kein Schwierigkeitsgrad gewählt", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    initialisiert Widgets
     */
    private void initWidgets(){
        singleplayer = (Button) findViewById(R.id.singleplayer);
        multiplayer = (Button) findViewById(R.id.multiplayer);
        exit = (Button) findViewById(R.id.exit);
        animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        fromSingleplayer = false;
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
                                // Singleplayer setzen
                                fromSingleplayer = true;
                                showDifficultyFragment();
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
                                fromSingleplayer = false;
                                Intent intentMulti = null;
                                intentMulti = new Intent(MainActivity.this, GPMultiActivity.class);
                                startActivity(intentMulti);
//
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

//    /*
//    Gewählte Geschwindigkeit setzen und Spiel-Activity starten
//     */
//    public void onLevelSelected(int level) {
//        selectedDifficulty = level;
//        Intent intent = null;
//
//        if (fromSingleplayer) {
//            intent = new Intent(this, GPSingleActivity.class);
//        } else {
//            intent = new Intent(this, GPMultiActivity.class);
//        }
//        intent.putExtra("selectedDifficulty", level);
//        startActivity(intent);
//    }

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
        FragmentManager fragmentManager = MainActivity.this.getFragmentManager();

        // Startet die Transaction des Fragments
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Zeigt das Fragment an
        difficultyFragment.show(fragmentTransaction, FRAGMENT_TAG);
    }

    /*
     Getter und Setter
      */
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

    public int getSelectedDifficulty() {
        return selectedDifficulty;
    }

    public void setSelectedDifficulty(int selectedDifficulty) {
        this.selectedDifficulty = selectedDifficulty;
    }

    public boolean isFromSingleplayer() {
        return fromSingleplayer;
    }

    public void setFromSingleplayer(boolean fromSingleplayer) {
        this.fromSingleplayer = fromSingleplayer;
    }

    public boolean isLevelSelected() {
        return levelSelected;
    }

    public void setLevelSelected(boolean levelSelected) {
        this.levelSelected = levelSelected;
    }


}
