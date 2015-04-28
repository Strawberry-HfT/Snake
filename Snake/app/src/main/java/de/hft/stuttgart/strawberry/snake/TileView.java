package de.hft.stuttgart.strawberry.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Juliano on 28.04.2015.
 */
public class TileView extends View {

    // Bitmap
    private Bitmap bitmap;

    // Größe der Fließe
    private int tileSize;

    // Anzahl der Fließen
    private final int X_TILE_COUNT = 30;
    private final int Y_TILE_COUNT = 20;

    // Rand außerhalbt
    private static int mXOffset;
    private static int mYOffset;

    // Hält die Positon des Bitmaps
    private int[][] mTileGrid;

    // Paint für die Fliesen
    private Paint mPaint = new Paint();

    // Konstruktor
    public TileView(Context context){
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Fliesen
        // Solange x kleiner als Anzahl der Fliesen horizontal
        for (int x = 0; x < X_TILE_COUNT; x += 1) {
            // Solange y kleiner als Anzahl der Fliesen vertikal
            for (int y = 0; y < Y_TILE_COUNT; y += 1) {
                if (mTileGrid[x][y] > 0) {
                    canvas.drawBitmap(this.bitmap,
                            mXOffset + x * tileSize,
                            mYOffset + y * tileSize,
                            mPaint);
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        int xPxSize = (int) Math.floor(w/this.X_TILE_COUNT);
        int yPxSize = (int) Math.floor(h/this.Y_TILE_COUNT);

        if(xPxSize > yPxSize){
            this.tileSize = yPxSize;
        } else{
            this.tileSize = xPxSize;
        }

        // Bitmap zum Test
        bitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.GREEN);

        // Rand außerhalb der Fließen
        mXOffset = ((w - (tileSize * X_TILE_COUNT)) / 2);
        mYOffset = ((h - (tileSize * Y_TILE_COUNT)) / 2);

        // Zweidimensionales Array mit der Anzahl der X und X Fliesen
        mTileGrid = new int[X_TILE_COUNT][Y_TILE_COUNT];
        clearTiles();
    }

    // TODO
    public void clearTiles() {
        for (int x = 0; x < X_TILE_COUNT; x++) {
            for (int y = 0; y < Y_TILE_COUNT; y++) {
                setTile(1, x, y);
            }
        }
    }

    // TODO
    public void setTile(int tileindex, int x, int y) {
        mTileGrid[x][y] = tileindex;
    }
}
