package de.hft.stuttgart.strawberry.views;

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
    private Bitmap berryBitmap;

    // Größe der Fließe
    private int tileSize;

    // Anzahl der Fließen
    public final static int X_TILE_COUNT = 34;
    public final static int Y_TILE_COUNT = 20;

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

        // Zweidimensionales Array mit der Anzahl der X und X Fliesen
        mTileGrid = new int[X_TILE_COUNT][Y_TILE_COUNT];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Spielfeld wird gezeichnet
        // Solange x kleiner als Anzahl der Fliesen horizontal
        for (int x = 0; x < X_TILE_COUNT; x += 1) {
            // Solange y kleiner als Anzahl der Fliesen vertikal
            for (int y = 0; y < Y_TILE_COUNT; y += 1) {
                // Wenn Wert 0, dann normale Fliese
                if (mTileGrid[x][y] == 0) {
                    // Zeichnet die Bitmaps, Rand + Fliesengröße * anzahl der Fließen in x oder y-Richtung
                    canvas.drawBitmap(this.bitmap,
                            mXOffset + x * tileSize,
                            mYOffset + y * tileSize,
                            mPaint);
                } if (mTileGrid[x][y] == 2){
                    // Wenn Wert zwei dann Schlangenelement
                    Bitmap snake = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
                    snake.eraseColor(Color.BLUE);
                    canvas.drawBitmap(snake,
                            mXOffset + x * tileSize,
                            mYOffset + y * tileSize,
                            mPaint);
                } if (mTileGrid[x][y] == 1){
                    // Wenn 1 dann Beere
                    this.berryBitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
                    berryBitmap.eraseColor(Color.RED);
                    canvas.drawBitmap(this.berryBitmap,
                            mXOffset + x * tileSize,
                            mYOffset + y * tileSize,
                            mPaint);
                }
            }
        }
        // Setzt alle Fliesen des Spielfeldes wieder auf 0 nach jedem Zeichnen
        clearTiles();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        // Berechnet die mögliche Größe einer Fliese in x-Richtung
        int xPxSize = (int) Math.floor(w/this.X_TILE_COUNT);
        // Berechnet die mögliche Größe einer Fliese in y-Richtung
        int yPxSize = (int) Math.floor(h/this.Y_TILE_COUNT);

        // Der kleinere Wert wird als Fliesengröße gewählt
        if(xPxSize > yPxSize){
            this.tileSize = yPxSize;
        } else{
            this.tileSize = xPxSize;
        }

        // Bitmap zum Test, macht alle Hintergrundfließen Grün
        bitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.GREEN);

        // Rand außerhalb der Fließen
        mXOffset = ((w - (tileSize * X_TILE_COUNT)) / 2);
        mYOffset = ((h - (tileSize * Y_TILE_COUNT)) / 2);

//        clearTiles();
    }

    // TODO
    public void clearTiles() {
        for (int x = 0; x < X_TILE_COUNT; x++) {
            for (int y = 0; y < Y_TILE_COUNT; y++) {
                setTile(0, x, y);
            }
        }
    }

    // TODO
    public void setTile(int tileindex, int x, int y) {
        mTileGrid[x][y] = tileindex;
    }

    // Setter und Getter
    public int[][] getmTileGrid() {
        return mTileGrid;
    }

    public void setmTileGrid(int[][] mTileGrid) {
        this.mTileGrid = mTileGrid;
    }
}
