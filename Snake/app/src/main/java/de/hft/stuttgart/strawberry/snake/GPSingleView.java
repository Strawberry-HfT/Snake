package de.hft.stuttgart.strawberry.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Verwaltet die Schlange und die Erdbeere.
 * Created by Tommy_2 on 26.03.2015.
 */
public class GPSingleView extends View {

    // Variablen
    private Snake snake;
    private Strawberry strawberry;

    private Bitmap berryBitmap;
    private Bitmap snakeBitmap;

    /*
    Constructor der Context übergeben bekommt.
    Der Context ist die aufrufende Activity (GPSingleActivity)
     */
    public GPSingleView(Context context, Snake snake, Strawberry strawberry) {
        super(context);
        this.snake = snake;
        this.strawberry = strawberry;
        this.berryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.strawberry_icon);
        this.setBackgroundResource(R.drawable.gameplay_background);
    }

    // Zeichnet die View
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        snake.drawSnakeDots(canvas);
        strawberry.drawStrawberry(canvas, this.getBerryBitmap());
    }

    public Bitmap getBerryBitmap() {
        return berryBitmap;
    }

    public void setBerryBitmap(Bitmap berryBitmap) {
        this.berryBitmap = berryBitmap;
    }

    public Bitmap getSnakeBitmap() {
        return snakeBitmap;
    }

    public void setSnakeBitmap(Bitmap snakeBitmap) {
        this.snakeBitmap = snakeBitmap;
    }
}
