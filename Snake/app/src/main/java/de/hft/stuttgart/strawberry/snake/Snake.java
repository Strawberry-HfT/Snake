package de.hft.stuttgart.strawberry.snake;

import android.graphics.Point;

import java.util.ArrayList;

import de.hft.stuttgart.strawberry.views.TileView;

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
                position.get(0).y = TileView.Y_TILE_COUNT-1;
            } else {
                this.position.get(0).y -= 1;
            }
        }
        // Nach Unten
        if (direction.isDown()) {
            if(position.get(0).y >= TileView.Y_TILE_COUNT-1){
                position.get(0).y = 0;
            } else {
                this.position.get(0).y += 1;
            }
        }
        // Nach Rechts
        if (direction.isRight()) {
            if (position.get(0).x >= TileView.X_TILE_COUNT-1) {
                position.get(0).x = 0;
            } else {
                this.position.get(0).x += 1;
            }
        }

        // Nach Links
        if (direction.isLeft()) {
            if (position.get(0).x <= 0) {
                position.get(0).x = TileView.X_TILE_COUNT-1;
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
