//package de.hft.stuttgart.strawberry.views;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//
//import de.hft.stuttgart.strawberry.snake.R;
//
///**
// * Verwaltet die Schlange und die Erdbeere.
// * Created by Tommy_2 on 26.03.2015.
// */
//public class GPSingleView extends TileView {
//
//    // Bild für die Beere
//    private Bitmap berryBitmap;
//    private Bitmap snakeBitmap;
//
//    // Hält die berechnete Fließenfröße aus der Superklasse
//    private int tileSize;
//
//    // Rand außerhalb des Spielfeldes
//    private int xOffset;
//    private int yOffset;
//
//    // Spielfeld in einem zweidimensionalen Array
//    private int[][] tileGrid = new int[TileView.X_TILE_COUNT][TileView.Y_TILE_COUNT];
//
//    // Paint für die Fliesen
//    private Paint mPaint = new Paint();
//
//    // Hintergrund wird nur 1 mal gezeichnet
//    private boolean firstcall = true;
//
//    /*
//        Constructor der Context übergeben bekommt.
//        Der Context ist die aufrufende Activity (GPSingleActivity)
//         */
//    public GPSingleView(Context context) {
//        super(context);
//    }
//
//    private void initBitmaps() {
//        // Schlange
//        this.snakeBitmap =Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
//        snakeBitmap.eraseColor(Color.BLUE);
//
//        // Beere
//        this.berryBitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
//        berryBitmap.eraseColor(Color.RED);
//
//    }
//
//    // Zeichnet die View
//    @Override
//    protected void onDraw(Canvas canvas) {
//        // Hintergrund wird nur beim ersten Aufruf gezeichnet
//        if(firstcall) {
//        super.onDraw(canvas);
//            firstcall = false;
//        }
//
//        for (int x = 0; x < X_TILE_COUNT; x += 1) {
//            // Solange y kleiner als Anzahl der Fliesen vertikal
//            for (int y = 0; y < Y_TILE_COUNT; y += 1) {
//                if (tileGrid[x][y] == 2){
//                    // Wenn 2 dann Schlangenelement
//                    canvas.drawBitmap(this.snakeBitmap,
//                            xOffset + x * tileSize,
//                            yOffset + y * tileSize,
//                            mPaint);
//                    // Setzt Wert direkt nach dem Zeichnen wieder auf 0
//                    tileGrid[x][y] = 0;
//                } if (tileGrid[x][y] == 1){
//                    // Wenn 1 dann Beere
//                    canvas.drawBitmap(this.berryBitmap,
//                            xOffset + x * tileSize,
//                            yOffset + y * tileSize,
//                            mPaint);
//                    // Setzt Wert direkt nach dem Zeichnen wieder auf 0
//                    tileGrid[x][y] = 0;
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        this.tileSize = super.getTileSize();
//        this.xOffset = super.getmXOffset();
//        this.yOffset = super.getmYOffset();
//
//        initBitmaps();
//    }
//
//    // Getter
//    public int[][] getTileGrid() {
//        return tileGrid;
//    }
//}
