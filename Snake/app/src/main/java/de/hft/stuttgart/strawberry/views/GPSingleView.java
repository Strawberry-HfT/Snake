package de.hft.stuttgart.strawberry.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import de.hft.stuttgart.strawberry.snake.R;

/**
 * Verwaltet die Schlange und die Erdbeere.
 * Created by Tommy_2 on 26.03.2015.
 */
public class GPSingleView extends TileView {

//    // Variablen
//    private Snake snake;
//    private Strawberry strawberry;

    // Bilddateien
    private Bitmap berryBitmap;
    private Bitmap snakeBitmap;

    /*
        Constructor der Context übergeben bekommt.
        Der Context ist die aufrufende Activity (GPSingleActivity)
         */
    public GPSingleView(Context context) {
        super(context);

//        this.snake = snake;
//        this.strawberry = strawberry;
        this.berryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.strawberry_icon);

        // TODO Wieder einkommentierten, zu Testzwecken auskommentiert
//        this.setBackgroundResource(R.drawable.gameplay_background);
    }

    // Zeichnet die View
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        snake.drawSnakeDots(canvas);
//        strawberry.drawStrawberry(canvas, this.getBerryBitmap());
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
