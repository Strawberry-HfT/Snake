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

    // Position der Schlangenglieder
    private int xSnakePos, ySnakePos;

    // Position der Erdbeere
    private int xBerryPos, yBerryPos;

    // Bildschirmauflösung
    private Point displaySize;

    // Kollisionsgeber
    private boolean collides;

    // anderes zu überprüfendes Objekt
    private Point otherCheckedObj;

    /*
    Konsturktor dieser View
     */
    public DrawGPView(Context ctx, Snake currSnake, Point displaySize) {
        super(ctx);
        setBackgroundResource(R.drawable.gameplay_background);

        paintSnakeItem = new Paint();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.strawberry_icon);
        if (currSnake != null && currSnake.getPosition() != null) {
            snake = currSnake;
        }
        paintSnakeItem.setColor(Color.BLACK);
        this.displaySize = displaySize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSnake(canvas);

        // Hier werden testweise mal 50 Erdbeeren erstellt um zu schauen wo die so verteilt werden
        for (int i = 0; i < 50; i++) {
            createBerryPosition();
            canvas.drawBitmap(bitmap, xBerryPos, yBerryPos, null);
        }
    }
    /*
    Zeichnet die Schlange
     */
    private void drawSnake(Canvas canvas) {
        for (Point currPosition : snake.getPosition()) {
            xSnakePos = currPosition.x;
            ySnakePos = currPosition.y;
            canvas.drawCircle(xSnakePos, ySnakePos, snakeItemSize, paintSnakeItem);
        }
    }

    /*
    Erstellt eine Erdbeere auf dem Spielfeld
     */
    private void createBerryPosition() {

        int maxWidth = displaySize.x;
        int maxHeight = displaySize.y;

        xBerryPos = 0;
        yBerryPos = 0;

        xBerryPos = (int) (Math.random() * 50) * 38;
        yBerryPos = (int) (Math.random() * 50) * 18;

        otherCheckedObj = new Point(xBerryPos, yBerryPos);

        checkCollision(snake, otherCheckedObj);

        if ((xBerryPos > maxWidth || yBerryPos > maxHeight) || collides) {
            Log.i("BerryPosition", "Position ausserhalb der Anzeige oder Kollision");
            createBerryPosition();
        }
    }

    /*
    Überprüfung, ob die Schlange mit sich selber, der Erdbeere oder der Wand kollidiert
     */
    private boolean checkCollision(Snake snake, Point otherObj) {
        collides = false;
        //TODO nicht genug Parameter zur Kontrolle, da nur die genauen Pixel hier beachtet werden aber nicht wie groß die Bilder dann sind und ob sie sich übeschneiden
        for (Point currP : snake.getPosition()) {
            if (currP.x == otherObj.x && currP.y == otherObj.y) {
                collides = true;
                Log.i("checkCollision", "Damn it collides!");
            }
        }
        return  collides;
    }

}
