package de.hft.stuttgart.strawberry.snake;

import android.graphics.Point;

import java.util.ArrayList;

import de.hft.stuttgart.strawberry.common.Constants;
import de.hft.stuttgart.strawberry.common.Movement;
import de.hft.stuttgart.strawberry.common.Strawberry;
import de.hft.stuttgart.strawberry.views.GPSingleSurfaceView;

/**
 * Created by Tommy_2 on 26.03.2015.
 * Erstellt die Schlange als Objekt.
 */
public class Snake {

    // Spielfeld
    private int [][] playingField;

    // Hält die Point-Positionen der Schlange auf dem Spielfeld
    private ArrayList<Point> position = new ArrayList<Point>();

    // Anzahl der Punkte
    private int dots;

    // Schwierigkeit des Spiels
    private int level;

    private boolean firstPlayer = false;

    // Konstruktor
    public Snake(int dots, int [][]playingField, boolean isfirstPlayer){
        this.dots = dots;
        this.playingField = playingField;
        firstPlayer = isfirstPlayer;

        initStartPositions(this.dots);
    }

    private void initStartPositions(int dots){
        // Berechnet Startposition auf dem Spielfeld für dahinter liegende Punkte
        for(int i = 0; i < dots; i++){
            Point point = new Point();
            if (firstPlayer) {
                point.x = 5;
                point.y = 10;
                point.x -= i;
                position.add(point);
            } else {
                point.x = 29;
                point.y = 10;
                point.x += i;
                position.add(point);
            }
        }
    }

    // Bewegt die Schlange
    public void moveSnake(Movement direction){

        /*
         Durchläuft die Positionen Rückwärts (Vom letzten zum ersten Wert)
         Dise Schleife zieht alle Dots hinter dem Kopf nach
          */
        for(int i = this.position.size()-1; i > 0; i--){
            this.position.get(i).x = this.position.get(i-1).x;
            this.position.get(i).y = this.position.get(i-1).y;
        }

        // Setzt die neuen Koordinaten für die Bewegung
        // Nach Oben
        if (direction.isUp()) {
            if(position.get(0).y <= 0){
                position.get(0).y = Constants.YTILE_COUNT-1;
            } else {
                this.position.get(0).y -= 1;
            }
        }
        // Nach Unten
        if (direction.isDown()) {
            if(position.get(0).y >= Constants.YTILE_COUNT-1){
                position.get(0).y = 0;
            } else {
                this.position.get(0).y += 1;
            }
        }
        // Nach Rechts
        if (direction.isRight()) {
            if (position.get(0).x >= Constants.XTILE_COUNT-1) {
                position.get(0).x = 0;
            } else {
                this.position.get(0).x += 1;
            }
        }

        // Nach Links
        if (direction.isLeft()) {
            if (position.get(0).x <= 0) {
                position.get(0).x = Constants.XTILE_COUNT-1;
            } else {
                this.position.get(0).x -= 1;
            }
        }

        // Markiert die Koordinaten auf dem Spielfeld
        for(int i = 0; i<position.size();i++){
            playingField[position.get(i).x][position.get(i).y]= 2;
        }
}

    public void checkCollisionBerry(Strawberry berry){
        Point berryPosition = berry.getBerryPosition();
        if(position.get(0).x == berryPosition.x && position.get(0).y == berryPosition.y){
            position.add(new Point());
            berry.createBerryPosition();
        }
    }

    public boolean checkCollisionSnake(){
        boolean collison = false;
        for(int i = 1; i< position.size();i++){
            if(position.get(0).x == position.get(i).x && this.position.get(0).y == position.get(i).y){
                collison = true;
            }
        }
        return collison;
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
