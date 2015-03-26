package de.hft.stuttgart.strawberry.snake;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    //Buttons
    private Button singleplayer;
    private Button multiplayer;
    private Button exit;

    /*
    Methode wird beim Start der Applikation aufgerufen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        // Initialisiert die Widgets der Activity
        this.initWidgets();

        // Initialisiert die Handler der Widgets
        this.initWidgetHandlers();

    }

    /*
    Methoder Erzeu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    private void initWidgets(){
        singleplayer = (Button) findViewById(R.id.singleplayer);
        multiplayer = (Button) findViewById(R.id.multiplayer);
        exit = (Button) findViewById(R.id.exit);
    }

    private void initWidgetHandlers(){

        // Klicklistener für Singleplayer Button
        singleplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DifficultyFragement difficultyFragement = new DifficultyFragement();
                // TODO Fragmentmanager
            }
        });

        // Klicklistener für Multiplayer Button
        multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}
