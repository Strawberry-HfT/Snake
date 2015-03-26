package de.hft.stuttgart.strawberry.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Tommy_2 on 26.03.2015.
 */
public class DrawGPView extends View {

    Paint paintSnakeItem = new Paint();
    Paint paintStrawberry = new Paint();

    public DrawGPView(Context ctx) {
        super(ctx);
        paintSnakeItem.setColor(Color.YELLOW);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(100,100,20,paintSnakeItem);
    }
}
