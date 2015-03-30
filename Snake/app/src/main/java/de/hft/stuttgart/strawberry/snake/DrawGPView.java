package de.hft.stuttgart.strawberry.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

/**
 * Zeichnet die Schlange und die Erdbeere.
 * Created by Tommy_2 on 26.03.2015.
 */
public class DrawGPView extends View {

    private static final int snakeItemSize = 25;

    private Paint paintSnakeItem = new Paint();
    private Paint paintStrawberry = new Paint();

    private Snake snake;
    private int xPos, yPos;

    public DrawGPView(Context ctx, Snake currSnake) {
        super(ctx);
        if (currSnake != null && currSnake.getPosition() != null) {
            snake = currSnake;
        }
        paintSnakeItem.setColor(Color.BLACK);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Point currPosition : snake.getPosition()) {
            xPos = currPosition.x;
            yPos = currPosition.y;
            canvas.drawCircle(xPos, yPos, snakeItemSize, paintSnakeItem);
        }
        canvas.save();

    }

}
