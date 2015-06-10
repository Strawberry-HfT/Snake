package de.hft.stuttgart.strawberry.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import de.hft.stuttgart.strawberry.activities.GPSingleActivity;
import de.hft.stuttgart.strawberry.common.Constants;
import de.hft.stuttgart.strawberry.common.Strawberry;
import de.hft.stuttgart.strawberry.snake.Snake;

/**
 * Created by Juliano on 06.06.2015.
 */
public class GPSingleSurfaceView extends SurfaceView implements Runnable{

    // Activity
    GPSingleActivity activity;

    // Bild für die Beere
    private Bitmap berryBitmap;
    private Bitmap snakeBitmap;
    private Bitmap backgroundBitmap;

    // Spielobjekte
    private Snake snake;
    private Strawberry strawberry;

    // Größe der Fliese, wird berechnet
    private int tTileSize;

    // Rand außerhalbt
    private int tXOffset;
    private int tYOffset;

    // Hält die Positon des Bitmaps
    private int[][] tTileGrid;

    // Paint für die Fliesen
    private Paint tPaint = new Paint();

    // Schwierigkeit
    int difficulty;

    // Für SurfaceView
    SurfaceHolder surfaceHolder;
    Thread thread = null;
    boolean isRunning = false;

    // Constructor
    public GPSingleSurfaceView(Context context){
        super(context);

        activity = (GPSingleActivity)context;
        // Zweidimensionales Array mit der Anzahl der X und X Fliesen
        tTileGrid = new int[Constants.XTILE_COUNT][Constants.YTILE_COUNT];
        // Schwierigkeit aus Activity
        this.difficulty = this.activity.getSelectedDifficulty();
        surfaceHolder = getHolder();

    }

    /*
    Hier wird das Spiel in einem Thread gezeichnet
     */
    @Override
    public void run() {
        while(isRunning) {
            if (!surfaceHolder.getSurface().isValid()) {
                continue;
            }
            Canvas canvas = surfaceHolder.lockCanvas();

            strawberry.drawBerry();
            snake.moveSnake(activity.getDirection());
            snake.checkCollisionBerry(strawberry);
            if (snake.checkCollisionSnake()) {
                activity.finish();
        }

            for (int x = 0; x < Constants.XTILE_COUNT; x += 1) {
                // Solange y kleiner als Anzahl der Fliesen vertikal
                for (int y = 0; y < Constants.YTILE_COUNT; y += 1) {
                    if(tTileGrid[x][y] == 0){
                        // Zeichnet die Bitmaps, Rand + Fliesengröße * anzahl der Fließen in x oder y-Richtung
                        canvas.drawBitmap(this.backgroundBitmap,
                                tXOffset + x * tTileSize,
                                tYOffset + y * tTileSize,
                                tPaint);
                    }if( tTileGrid[x][y] == 2){
                        // Wenn 2 dann Schlangenelement
                        canvas.drawBitmap(this.snakeBitmap,
                                tXOffset + x * tTileSize,
                                tYOffset + y * tTileSize,
                                tPaint);
                        // Setzt Wert direkt nach dem Zeichnen wieder auf 0
                        tTileGrid[x][y] = 0;
                    } if (tTileGrid[x][y] == 1){
                        // Wenn 1 dann Beere
                        canvas.drawBitmap(this.berryBitmap,
                                tXOffset + x * tTileSize,
                                tYOffset + y * tTileSize,
                                tPaint);
                        // Setzt Wert direkt nach dem Zeichnen wieder auf 0
                        tTileGrid[x][y] = 0;
                    }
                }
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
            try {
                Thread.sleep(difficulty);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Wird von der Activity aufgerufen, wenn Pause gedrückt wird, um den Thread zu stoppen
    public void pause(){
        this.isRunning = false;
        while(true){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
        thread = null;
    }

    // Wird von der Activity aufgerufen, wenn das Spiel gestartet wird. Startet den Thread
    public void resume(){
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    /* Misst mein beim Spielstart die Displaygröße und berechnet so die mögliche
     Fließengröße
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Berechnet die mögliche Größe einer Fliese in x-Richtung
        int xPxSize = (int) Math.floor(w/Constants.XTILE_COUNT);
        // Berechnet die mögliche Größe einer Fliese in y-Richtung
        int yPxSize = (int) Math.floor(h/Constants.YTILE_COUNT);

        // Der kleinere Wert wird als Fliesengröße gewählt
        if(xPxSize > yPxSize){
            this.tTileSize = yPxSize;
        } else{
            this.tTileSize = xPxSize;
        }

        // Rand außerhalb der Fließen
        tXOffset = ((w - (tTileSize * Constants.XTILE_COUNT)) / 2);
        tYOffset = ((h - (tTileSize * Constants.YTILE_COUNT)) / 2);

        initBitmaps();
        initSnakeAndBerry();
    }

    // Initialisiert die Bitmaps zum Zeichen
    private void initBitmaps() {
        // Schlange
        this.snakeBitmap =Bitmap.createBitmap(tTileSize, tTileSize, Bitmap.Config.ARGB_8888);
        snakeBitmap.eraseColor(Color.BLUE);

        // Beere
        this.berryBitmap = Bitmap.createBitmap(tTileSize, tTileSize, Bitmap.Config.ARGB_8888);
        berryBitmap.eraseColor(Color.RED);

        // Bitmap zum Test, macht alle Hintergrundfließen Grün
        backgroundBitmap = Bitmap.createBitmap(tTileSize, tTileSize, Bitmap.Config.ARGB_8888);
        backgroundBitmap.eraseColor(Color.GREEN);
    }

    // Initialisiert die Schlange und die Beere
    private void initSnakeAndBerry() {
        this.snake = new Snake(3, gettTileGrid(), true);
        this.strawberry = new Strawberry(gettTileGrid());
    }

    // Getter
    public int[][] gettTileGrid() {
        return tTileGrid;
    }


}
