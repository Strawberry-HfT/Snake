package de.hft.stuttgart.strawberry.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import de.hft.stuttgart.strawberry.activities.GPMultiActivity;
import de.hft.stuttgart.strawberry.common.Constants;
import de.hft.stuttgart.strawberry.common.Strawberry;
import de.hft.stuttgart.strawberry.snake.Snake;

/**
 * Created by Tommy_2 on 08.06.2015.
 */
public class GPMultiSurfaceView extends SurfaceView implements Runnable {

    // TAG f�r den Logger
    private static final String TAG = GPMultiSurfaceView.class.getSimpleName();

    // FirstCall
    boolean firstCall = true;

    // Activity
    GPMultiActivity activity;

    // Bild f�r die Beere
    private Bitmap berryBitmap;
    private Bitmap snakeBitmap;
    private Bitmap backgroundBitmap;

    // Spielobjekte
    private Snake snake;
    private Strawberry strawberry;

    // Groesse der Fliese, wird berechnet
    private int tTileSize;

    // Rand auserhalbt
    private int tXOffset;
    private int tYOffset;

    // Haelt die Positon des Bitmaps
    private int[][] tTileGrid;

    // Paint fuer die Fliesen
    private Paint tPaint = new Paint();

    // Schwierigkeit
    int difficulty;

    // Fuer SurfaceView
    SurfaceHolder surfaceHolder;
    Thread thread = null;
    boolean isRunning = false;

    // Constructor
    public GPMultiSurfaceView(Context context, long startTime){
        super(context);

        // Holt Activity in die View
        activity = (GPMultiActivity)context;

        // Zweidimensionales Array mit der Anzahl der X und X Fliesen
        tTileGrid = new int[Constants.XTILE_COUNT][Constants.YTILE_COUNT];

        // Schwierigkeit aus Activity
        this.difficulty = this.activity.getLevelSpeed();
        surfaceHolder = getHolder();

    }

    /*
    Hier wird das Spiel in einem Thread gezeichnet
     */
    @Override
    public void run() {
        Log.d(TAG, "start drawing thread");
        while(isRunning) {
            if (!surfaceHolder.getSurface().isValid()) {
                continue;
            }
            Canvas canvas = surfaceHolder.lockCanvas();

            // Zeichnet die Beere, aber nicht beim ersten Durchgang
            if(!firstCall){
                strawberry.drawBerry();
            }

            // Beim ersten Durchlauf erzeugt Player 1 die Beere und sendet sie an 2.Player
            if(activity.isFirstPlayer() && firstCall){
                firstCall = false;
                // Erstellt die Positoin der ersten Beere
                strawberry.createBerryPosition();
                // Zeichne die eigene Beere, beim ersten Durchgang
                strawberry.drawBerry();
                // Holt den Wert der ersten Beere
                Point berryPosition = strawberry.getBerryPosition();
                // Sendet die Beere
                StringBuffer sB = new StringBuffer();
                sB.append(berryPosition.x+":");
                sB.append(berryPosition.y);
                activity.sendNotification(Constants.NOTIFIER_FIRST_BERRY, sB.toString());
            }

            // Bei einer Collision wird die Beere neu erzeugt und an anderen Spieler gesendet
            if(snake.checkCollisionBerrySecondPlayer(strawberry)){
                // Aktualisiert die Positon der Beere
                strawberry.createBerryPosition();
                // Holt den Point
                Point berryPosition = strawberry.getBerryPosition();
                // Senden
                StringBuffer sB = new StringBuffer();
                sB.append(berryPosition.x+":");
                sB.append(berryPosition.y);
                activity.sendNotification(Constants.NOTIFIER_BERRY_HIT,sB.toString());
            }

            snake.moveSnake(activity.getDirection());
            if (snake.checkCollisionSnake()) {
                activity.finish();
            }

            for (int x = 0; x < Constants.XTILE_COUNT; x += 1) {
                // Solange y kleiner als Anzahl der Fliesen vertikal
                for (int y = 0; y < Constants.YTILE_COUNT; y += 1) {
                    if(tTileGrid[x][y] == 0){
                        // Zeichnet die Bitmaps, Rand + Fliesengr��e * anzahl der Flie�en in x oder y-Richtung
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

    // Wird von der Activity aufgerufen, wenn Pause gedr�ckt wird, um den Thread zu stoppen
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
        Log.d(TAG, "resume()");
//        activity.sendPosition();
        isRunning = true;
        thread = new Thread(this);
//        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    /* Misst mein beim Spielstart die Displaygr��e und berechnet so die m�gliche
     Flie�engr��e
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "invoke onSizeChanged()");
        // Berechnet die m�gliche Gr��e einer Fliese in x-Richtung
        int xPxSize = (int) Math.floor(w/Constants.XTILE_COUNT);
        // Berechnet die m�gliche Gr��e einer Fliese in y-Richtung
        int yPxSize = (int) Math.floor(h/Constants.YTILE_COUNT);

        // Der kleinere Wert wird als Fliesengr��e gew�hlt
        if(xPxSize > yPxSize){
            this.tTileSize = yPxSize;
        } else{
            this.tTileSize = xPxSize;
        }

        // Rand au�erhalb der Flie�en
        tXOffset = ((w - (tTileSize * Constants.XTILE_COUNT)) / 2);
        tYOffset = ((h - (tTileSize * Constants.YTILE_COUNT)) / 2);

        initBitmaps();
        initSnakeAndBerry();

        // Startet das Zeichnen
        this.resume();
    }

    // Initialisiert die Bitmaps zum Zeichen
    private void initBitmaps() {
        // Schlange
        this.snakeBitmap =Bitmap.createBitmap(tTileSize, tTileSize, Bitmap.Config.ARGB_8888);
        snakeBitmap.eraseColor(Color.BLUE);

        // Beere
        this.berryBitmap = Bitmap.createBitmap(tTileSize, tTileSize, Bitmap.Config.ARGB_8888);
        berryBitmap.eraseColor(Color.RED);

        // Bitmap zum Test, macht alle Hintergrundflie�en Gr�n
        backgroundBitmap = Bitmap.createBitmap(tTileSize, tTileSize, Bitmap.Config.ARGB_8888);
        backgroundBitmap.eraseColor(Color.GREEN);
    }

    // Initialisiert die Schlange und die Beere
    private void initSnakeAndBerry() {
        this.snake = new Snake(3, gettTileGrid(), activity.isFirstPlayer());
        this.strawberry = new Strawberry(gettTileGrid());
    }

    // Getter
    public int[][] gettTileGrid() {
        return tTileGrid;
    }

    public Snake getSnake() {
        return snake;
    }

    public Strawberry getStrawberry() {
        return strawberry;
    }

    public void setFirstCall(boolean firstCall) {
        this.firstCall = firstCall;
    }
}
