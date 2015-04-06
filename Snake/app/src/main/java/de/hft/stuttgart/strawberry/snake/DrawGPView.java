package de.hft.stuttgart.strawberry.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

/**
 * Verwaltet die Schlange und die Erdbeere.
 * Created by Tommy_2 on 26.03.2015.
 */
public class DrawGPView extends View {

    // Maximale Länge der Schlange
    private static final int snakeItemSize = 25;

    // Komponente, um einen schwarzen Kreis zu zeichnen
    private Paint paintSnakeItem;

    // Bitmap zur Importierung der Erdbeere
    private Bitmap bitmap;

    // Das Schlangenobjekt
    private Snake snake;

    private Strawberry berry;

    // PositionListener
    private PosititonListener positionListener;

    /*
    Konsturktor dieser View
     */
    public DrawGPView(Context ctx, Snake currSnake, Strawberry currBerry, Point displaySize) {
        super(ctx);

        // Hintergrund setzen
        setBackgroundResource(R.drawable.gameplay_background);

        //TODO hier sollte noch abgefangen werden was passiert wenn currSnake und currBerry null sind
        // Schlange initialisieren
        paintSnakeItem = new Paint();
        paintSnakeItem.setColor(Color.BLACK);
        if (currSnake != null && currSnake.getPosition() != null) {
            snake = currSnake;
        }

        // Erdbeere initialisieren
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.strawberry_icon);
        if (currBerry != null && currBerry.getPosition() != null) {
            berry = currBerry;
        }
        Point displaySize1 = displaySize;
    }

    /*
    Zeichnet die Schlange
     */
    private void drawSnake(Canvas canvas) {
        for (Point currPosition : snake.getPosition()) {
            int xSnakePos = currPosition.x;
            int ySnakePos = currPosition.y;
            canvas.drawCircle(xSnakePos, ySnakePos, snakeItemSize, paintSnakeItem);
        }
    }

    /*
    Erstellt eine Erdbeere auf dem Spielfeld
     */
    private void drawBerry(Canvas canvas) {
        int xBerryPos = berry.getPosition().x;
        int yBerryPos = berry.getPosition().y;
        canvas.drawBitmap(bitmap, xBerryPos, yBerryPos, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (positionListener != null) {
            positionListener.onPositionChanged();
        }
        try {
            drawSnake(canvas);
            drawBerry(canvas);
//            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Interface nur testweise erst mal hier
     */
    public interface PosititonListener {
        public void onPositionChanged();
    }

    public void setPositionListener(PosititonListener positionListener) {
        this.positionListener = positionListener;
    }
}
