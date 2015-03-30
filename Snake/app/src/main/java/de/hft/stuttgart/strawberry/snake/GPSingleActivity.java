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

    private void initSnake(ArrayList<Point> snakePos) {
        snake = new Snake();
        snake.setPosition(snakePos);
        start = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        bundle = getIntent().getExtras();
        int geschw = bundle.getInt(DifficultyFragement.BUNDLE_DIFFICULTY);
        snakeLength = 3;
        ArrayList<Point> startingPos = new ArrayList<Point>();

        //Bildshirmauflösung holen und ins erste Drittel beider Achsen setzen
        Display display = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        for (int i = 0; i < snakeLength; i++) {
            int xPos, yPos;
            // Startposition der Schlange
            if (start) {
                Point startPos = new Point();
                xPos = displaySize.x/3 - i * 50;
                yPos = displaySize.y/3;
                startPos.set(xPos, yPos);
                startingPos.add(startPos);
            }
        }
        this.initSnake(startingPos);

        DrawGPView view = new DrawGPView(this, snake);
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        this.setContentView(view);
    }

}
