package de.hft.stuttgart.strawberry.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Tommy_2 on 26.03.2015.
 * Erstellt die Schlange als Objekt.
 */
public class Snake {

    private ArrayList<Point> position = new ArrayList<Point>();
    private int level;

    public Snake(ArrayList<Point> pos){
        setPosition(pos);
    }

    // Zeichnet die Schlange
    public void drawSnakeDots(Canvas canvas){
        Paint snakePaint = new Paint();
        snakePaint.setColor(Color.BLUE);

        for(Point currentPoint : position){
            canvas.drawCircle(currentPoint.x,currentPoint.y,25,snakePaint);
        }
    }

    // Bewegt die Schlange
    public void moveSnake(){
        for (Point currItem : position) {
            currItem.x += 10;
        }
    }

    // Setter und Getter
    public ArrayList<Point> getPosition() {
        return position;
    }

    public void setPosition(ArrayList<Point> position) {
        this.position = position;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
