package de.hft.stuttgart.strawberry.snake;

import android.graphics.Point;

import java.util.ArrayList;

import de.hft.stuttgart.strawberry.common.Constants;
import de.hft.stuttgart.strawberry.common.Movement;
import de.hft.stuttgart.strawberry.common.Strawberry;

/**
 * Created by Tommy_2 on 26.03.2015.
 * Erstellt die Schlange als Objekt.
 */
public class Snake {

    // Spielfeld
    private int [][] playingField;

    // Hält die Point-Positionen der Schlange auf dem Spielfeld
    private ArrayList<Point> positionFirst = new ArrayList<Point>();

    // Hält die Punkte der zweiten Schlange
    private ArrayList<Point> positionSecond = new ArrayList<Point>();

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
                positionFirst.add(point);
            } else {
                point.x = 29;
                point.y = 10;
                point.x += i;
                positionFirst.add(point);
            }
        }
    }

    // Bewegt die Schlange
    public void moveSnake(Movement direction){

        /*
         Durchläuft die Positionen Rückwärts (Vom letzten zum ersten Wert)
         Dise Schleife zieht alle Dots hinter dem Kopf nach
          */
        for(int i = this.positionFirst.size()-1; i > 0; i--){
            this.positionFirst.get(i).x = this.positionFirst.get(i-1).x;
            this.positionFirst.get(i).y = this.positionFirst.get(i-1).y;
        }

        // Setzt die neuen Koordinaten für die Bewegung
        // Nach Oben
        if (direction.isUp()) {
            if(positionFirst.get(0).y <= 0){
                positionFirst.get(0).y = Constants.YTILE_COUNT-1;
            } else {
                this.positionFirst.get(0).y -= 1;
            }
        }
        // Nach Unten
        if (direction.isDown()) {
            if(positionFirst.get(0).y >= Constants.YTILE_COUNT-1){
                positionFirst.get(0).y = 0;
            } else {
                this.positionFirst.get(0).y += 1;
            }
        }
        // Nach Rechts
        if (direction.isRight()) {
            if (positionFirst.get(0).x >= Constants.XTILE_COUNT-1) {
                positionFirst.get(0).x = 0;
            } else {
                this.positionFirst.get(0).x += 1;
            }
        }

        // Nach Links
        if (direction.isLeft()) {
            if (positionFirst.get(0).x <= 0) {
                positionFirst.get(0).x = Constants.XTILE_COUNT-1;
            } else {
                this.positionFirst.get(0).x -= 1;
            }
        }

        // Markiert die Koordinaten auf dem Spielfeld
        for(int i = 0; i< positionFirst.size();i++){
            playingField[positionFirst.get(i).x][positionFirst.get(i).y]= 2;
        }

        // Markiert die Koordinaten der zweiten Schlange auf dem Spielfeld
        for(int i = 0; i< positionSecond.size();i++){
            playingField[positionSecond.get(i).x][positionSecond.get(i).y]= 3;
        }
}

    public boolean checkCollisionBerry(Strawberry berry){
        boolean collision = false;
        Point berryPosition = berry.getBerryPosition();
        if(positionFirst.get(0).x == berryPosition.x && positionFirst.get(0).y == berryPosition.y){
            positionFirst.add(new Point());
            berry.createBerryPosition();
            collision = true;
        }
        return  collision;
    }

    public boolean checkCollisionBerrySecondPlayer(Strawberry berry){
        boolean hit = false;

        Point berryPosition = berry.getBerryPosition();
        if(positionFirst.get(0).x == berryPosition.x && positionFirst.get(0).y == berryPosition.y){
            positionFirst.add(new Point());
            hit = true;
        }
        return hit;
    }

    // Prüft ob Schlange mit sich selbst kollidiert
    public boolean checkCollisionWithOwnSnake(){
        boolean collison = false;
        for(int i = 1; i< positionFirst.size();i++){
            if(positionFirst.get(0).x == positionFirst.get(i).x && this.positionFirst.get(0).y == positionFirst.get(i).y){
                collison = true;
            }
        }
        return collison;
    }

    // Prüft ob die erste Schlange mit der zweiten kollidiert
    public boolean checkCollisionWithSecondSnake(){
        boolean collision = false;
        for(int i = 0; i<positionSecond.size(); i++){
            if(positionFirst.get(0).x == positionSecond.get(i).x && positionFirst.get(0).y == positionSecond.get(i).y){
                collision = true;
            }
        }
        return collision;
    }

    // Setter und Getter
    public ArrayList<Point> getPositionFirst() {
        return positionFirst;
    }

    public void setPositionFirst(ArrayList<Point> positionFirst) {
        this.positionFirst = positionFirst;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setPositionSecond(ArrayList<Point> positionSecond) {
        this.positionSecond = positionSecond;
    }
}
