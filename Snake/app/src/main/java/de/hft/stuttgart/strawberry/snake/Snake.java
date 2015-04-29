package de.hft.stuttgart.strawberry.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Tommy_2 on 26.03.2015.
 * Erstellt die Schlange als Objekt.
 */
public class Snake {

    private int [][] playingField;

    // Hält die Point-Positionen der Schlange
    private ArrayList<Point> position = new ArrayList<Point>();

    // Anzahl der Punkte
    private int dots;

    // Schwierigkeit des Spiels
    private int level;

    // Konstruktor
    public Snake(int dots, int [][]playingField){
        this.dots = dots;
        this.playingField = playingField;

        initStartPositions(this.dots);
    }

    private void initStartPositions(int dots){

        // Berechnet koordinaten auf dem Spielfeld für dahinterligende Punkte
        for(int i = 0; i < dots; i++){
            // Startposition
            Point point = new Point();
            point.x = 5;
            point.y = 10;
            point.x -= i;
            position.add(point);
        }

        // Iteriert über die Anzahl der Punkte
//        for(int i = 0; i< dots; i++){
//            Point point = new Point();
//            point.x = 100-i*20;
//            point.y = 100;
//            this.position.add(point);
//        }
    }

//    // Zeichnet die Schlange
//    public void drawSnakeDots(Canvas canvas){
//        Paint snakePaint = new Paint();
//        snakePaint.setColor(Color.BLUE);
//
//        for(Point currentPoint : position){
//            canvas.drawCircle(currentPoint.x,currentPoint.y,20,snakePaint);
//        }
//    }

    // Bewegt die Schlange
    public void moveSnake(Movement direction){

        /*
         Durchläuft die Positionen Rückwärts (Vom letzten zum ersten Wert)
         Dise Schleife zieht alle Dots hinter dem Kopf nach
          */
//        for (int x = 0; x < TileView.X_TILE_COUNT; x++) {
//            for (int y = 0; y < TileView.Y_TILE_COUNT; y += 1) {
//            playingField[x][y]= 2;
//        }
//
//
        for(int i = this.position.size()-1; i > 0; i--){
            this.position.get(i).x = this.position.get(i-1).x;
            this.position.get(i).y = this.position.get(i-1).y;
        }

            if(direction.isUp()){
                this.position.get(0).y -=1;
            } if(direction.isDown()){
                this.position.get(0).y +=1;
            } if(direction.isRight()){
                this.position.get(0).x +=1;
            } if(direction.isLeft()){
                this.position.get(0).x -=1;
            }

        // Markiert die Koordinaten auf dem Spielfeld
        for(int i = 0; i<position.size();i++){
            playingField[position.get(i).x][position.get(i).y]= 2;
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
