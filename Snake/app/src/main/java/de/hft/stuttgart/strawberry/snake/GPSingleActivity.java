package de.hft.stuttgart.strawberry.snake;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Von hier aus wird das Game-Play des
 * Singleplayermodus ausgefuehrt.
 * Created by Tommy_2 on 26.03.2015.
 */
public class GPSingleActivity extends Activity {
    Bundle bundle = new Bundle();

    private Snake snake;
    private int snakeLength;
    private boolean start = true;
    private Point displaySize;

    private void initSnake(ArrayList<Point> snakePos) {
        snake = new Snake();
        snake.setPosition(snakePos);
        start = false;
    }

    private void getDisplaySize() {
        Display display = getWindowManager().getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);
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
        ArrayList<Point> startingPos = new ArrayList<Point>();

        //Bildshirmauflösung holen und ins erste Drittel beider Achsen setzen
        getDisplaySize();

        for (int j = 0; j < 5; j++) {
            int x = j * 50;

            for (int i = 0; i < snakeLength; i++) {
                int xPos, yPos;
                // Startposition der Schlange
                if (start) {
                    Point startPos = new Point();
                    xPos = x + displaySize.x/3 - i * 50;
                    yPos = displaySize.y/3;
                    startPos.set(xPos, yPos);
                    startingPos.add(startPos);
                }
            }
            this.initSnake(startingPos);

            DrawGPView view = new DrawGPView(this, snake, displaySize);
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            this.setContentView(view);
        }

    }

}
