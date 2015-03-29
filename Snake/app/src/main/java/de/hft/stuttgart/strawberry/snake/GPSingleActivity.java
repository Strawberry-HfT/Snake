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

    private void initSnake() {
        snake = new Snake();

        //das ist nur zum testen erstmal
        //hole erst Displayauflösung
        Display display = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        //Punkt in der mitte
        Point snakePosition = new Point();
        snakePosition.set(displaySize.x/2,displaySize.y/2);

        ArrayList<Point> startPosition = new ArrayList<Point>();
        startPosition.add(snakePosition);
        snake.setPosition(startPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        bundle = getIntent().getExtras();
        int geschw = bundle.getInt(DifficultyFragement.BUNDLE_DIFFICULTY);
        this.initSnake();

        DrawGPView view = new DrawGPView(this, snake);
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        this.setContentView(view);
    }

}
