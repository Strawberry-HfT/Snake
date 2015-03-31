package de.hft.stuttgart.strawberry.snake;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Main activity der Snake-Applikation
 */
public class MainActivity extends ActionBarActivity implements OnDataPass{

    //Buttons
    private Button singleplayer;
    private Button multiplayer;
    private Button exit;

    // Variablen
    private int geschwindigkeit;

    /*
    Methode wird beim Start der Applikation aufgerufen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Gibt das Bundle in die Superklasse
        super.onCreate(savedInstanceState);

        // Verbindung mit .xml-file
        this.setContentView(R.layout.activity_main);

        // Setzt die Rotation des Bildschims
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialisiert die Widgets der Activity
        this.initWidgets();

        // Initialisiert die Handler der Widgets
        this.initWidgetHandlers();

    }

    /*
    Beim Erstellen der OprionBar
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

        // Klicklistener für Multiplayer Button
        multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Noch nicht implementiert", Toast.LENGTH_SHORT).show();
            }
        });

        // Klicklistener für Exit Button
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });
    }

    @Override
    public void onDataPass(int geschwindigkeit) {
        this.setGeschwindigkeit(geschwindigkeit);
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
