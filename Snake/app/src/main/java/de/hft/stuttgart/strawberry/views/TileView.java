//package de.hft.stuttgart.strawberry.views;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.view.View;
//
///**
// * Created by Juliano on 28.04.2015.
// */
//public class TileView extends View {
//
//    // Bitmap
//    private Bitmap backgroundBitmap;
//
//    // Größe der Fließe, wird berechnet
//    private int tileSize;
//
//    // Anzahl der Fließen
//    public final static int X_TILE_COUNT = 34;
//    public final static int Y_TILE_COUNT = 20;
//
//    // Rand außerhalbt
//    private int mXOffset;
//    private int mYOffset;
//
//    // Hält die Positon des Bitmaps
//    private int[][] mTileGrid;
//
//    // Paint für die Fliesen
//    private Paint mPaint = new Paint();
//
//    // Konstruktor
//    public TileView(Context context){
//        super(context);
//        // Zweidimensionales Array mit der Anzahl der X und X Fliesen
//        mTileGrid = new int[X_TILE_COUNT][Y_TILE_COUNT];
//
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        // Bitmap zum Test, macht alle Hintergrundfließen Grün
//        backgroundBitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
//        backgroundBitmap.eraseColor(Color.GREEN);
//
//        // Spielfeld wird gezeichnet
//        // Solange x kleiner als Anzahl der Fliesen horizontal
//        for (int x = 0; x < X_TILE_COUNT; x += 1) {
//            // Solange y kleiner als Anzahl der Fliesen vertikal
//            for (int y = 0; y < Y_TILE_COUNT; y += 1) {
//
//                    // Zeichnet die Bitmaps, Rand + Fliesengröße * anzahl der Fließen in x oder y-Richtung
//                    canvas.drawBitmap(this.backgroundBitmap,
//                            mXOffset + x * tileSize,
//                            mYOffset + y * tileSize,
//                            mPaint);
//
//            }
//        }
//    }
//
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//
//        // Berechnet die mögliche Größe einer Fliese in x-Richtung
//        int xPxSize = (int) Math.floor(w/this.X_TILE_COUNT);
//        // Berechnet die mögliche Größe einer Fliese in y-Richtung
//        int yPxSize = (int) Math.floor(h/this.Y_TILE_COUNT);
//
//        // Der kleinere Wert wird als Fliesengröße gewählt
//        if(xPxSize > yPxSize){
//            this.tileSize = yPxSize;
//        } else{
//            this.tileSize = xPxSize;
//        }
//
//        // Rand außerhalb der Fließen
//        mXOffset = ((w - (tileSize * X_TILE_COUNT)) / 2);
//        mYOffset = ((h - (tileSize * Y_TILE_COUNT)) / 2);
//    }
//
//    // Setter und Getter
//    public int getmYOffset() {
//        return mYOffset;
//    }
//
//    public int getmXOffset() {
//        return mXOffset;
//    }
//
//    public int getTileSize() {
//        return tileSize;
//    }
//}
