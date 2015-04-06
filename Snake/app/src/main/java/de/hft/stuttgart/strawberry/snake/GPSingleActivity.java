package de.hft.stuttgart.strawberry.snake;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;

import java.util.ArrayList;

/**
 * Von hier aus wird das Game-Play des
 * Singleplayermodus ausgefuehrt.
 * Created by Tommy_2 on 26.03.2015.
 */
public class GPSingleActivity extends Activity {
    private Bundle bundle = new Bundle();
    private DrawGPView GPView;

    private Snake snake;
    private Strawberry sBerry;

    private int snakeLength;
    private int snakeItemNumbr;

    private boolean start = true;
    private boolean collides;

    private Point displaySize;

    private ArrayList<Point> startingPos;
    private Point berryPos;
    private Point newBerryPos;

    // Position der Schlangenglieder
    private int xSnakePos, ySnakePos;


    private void initSnake(ArrayList<Point> snakePos) {
        snake = new Snake();
        snake.setPosition(snakePos);
    }

    private void initBerry(Point position) {
        sBerry = new Strawberry();
        sBerry.setPosition(position);
    }

    private void getDisplaySize() {
        Display display = getWindowManager().getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);
    }

    private void createInitialPositions() {
        // Position für Schlange
        for (int i = 0; i < snakeLength; i++) {
            int xPos, yPos;
            // Startposition der Schlange
                Point startPos = new Point();
                xPos = snakeItemNumbr + displaySize.x / 3 - i * 50;
                yPos = displaySize.y / 3;
                startPos.set(xPos, yPos);
                startingPos.add(startPos);
        }
        this.initSnake(startingPos);

        this.createBerryPosition(null, startingPos);
        this.initBerry(berryPos);
        start = false;
    }

    private void createBerryPosition(Point oldBerry, ArrayList<Point> snake) {
        int maxWidth = displaySize.x;
        int maxHeight = displaySize.y;
        int xBerryPos;
        int yBerryPos;


        xBerryPos = (int) (Math.random() * 50) * 38;
        yBerryPos = (int) (Math.random() * 50) * 18;

        // Neue Koordinaten wenn ausserhalb des Displaybereichs
        if (xBerryPos > maxWidth || yBerryPos > maxHeight) {
            createBerryPosition(oldBerry, snake);
        }

        newBerryPos = new Point(xBerryPos, yBerryPos);

        // Überprüfung ob die Erdbeere an derselben stelle ist
        if (oldBerry != null) {
            checkCollision(oldBerry, newBerryPos);
        }

        // Überprüfung ob die Erdbeere auf der Schlange ertellt werden würde
        if (snake != null) {
            for (Point currSItem : snake) {
                checkCollision(currSItem, newBerryPos);
            }
        } else {
            this.finish();
            throw new RuntimeException("Snake is NULL!! Something went wrong!");
        }

        // Falls eine der Prüfungen fehlgeschlagen sind, neue Position
        if (!collides) {
            berryPos.set(xBerryPos, yBerryPos);
        } else {
            createBerryPosition(oldBerry, snake);
        }
    }

    /*
   Überprüfung, ob die Schlange mit sich selber, der Erdbeere oder der Wand kollidiert
    */
    private boolean checkCollision(Point onePoint, Point otherPoint) {
        collides = false;
        //TODO nicht genug Parameter zur Kontrolle, da nur die genauen Pixel hier beachtet werden aber nicht wie groß die Bilder dann sind und ob sie sich übeschneiden
        if (onePoint != null && otherPoint != null) {
            if (onePoint.x == otherPoint.x && onePoint.y == otherPoint.y) {
                collides = true;
                Log.i("checkCollision", "Damn it collides!");
            }
        }
        return  collides;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        bundle = getIntent().getExtras();
        // habe ich auskommentiert weil wenn ich über den multiplayer button
        // das Gameplay aufrufe bekomm ich ne exception. Das mach ich weils schneller geht
//        int geschw = bundle.getInt(DifficultyFragement.BUNDLE_DIFFICULTY);
        snakeLength = 3;
        startingPos = new ArrayList<Point>();
        berryPos = new Point();

        //Bildshirmauflösung holen und ins erste Drittel beider Achsen setzen
        getDisplaySize();
        createInitialPositions();

        GPView = new DrawGPView(GPSingleActivity.this, snake, sBerry, displaySize);
        GPView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        this.setContentView(GPView);
    }

    private int i = 0;
    @Override
    protected void onStart() {
        super.onStart();
        GPView.setPositionListener(new DrawGPView.PosititonListener() {
            @Override
            public void onPositionChanged() {

                Log.i("Interface Event", "Interface called the " + i + " time");
                i++;
            }
        });
    }
}
