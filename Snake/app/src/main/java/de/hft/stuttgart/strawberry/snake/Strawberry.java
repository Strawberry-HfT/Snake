package de.hft.stuttgart.strawberry.snake;

import android.graphics.Point;

/**
 * Created by Tommy_2 on 06.04.2015.
 * Erstellt die Erdbeere als Objekt
 */
public class Strawberry {

    // Spielfeld
    private int [][] playingField;
    private Point berryPosition;

    // Constructor
    public Strawberry(int [][]playingField){
        this.playingField = playingField;
        this.berryPosition = new Point();
        createBerryPosition();
    }

    public void createBerryPosition(){

        // Zufallspunkte
        int xKoordinate = (int) ((Math.random()* TileView.X_TILE_COUNT-1));
        int yKoordinate = (int) ((Math.random()* TileView.Y_TILE_COUNT-1));

        // Prüft ob Wert auf Schlange erzeugt wird
        if(playingField[xKoordinate][yKoordinate] != 2){
            this.berryPosition.x = xKoordinate;
            this.berryPosition.y = yKoordinate;
        } else {
            createBerryPosition();
        }
    }

    public void drawBerry(){
        this.playingField[berryPosition.x][berryPosition.y] = 1;
    }

    public Point getBerryPosition() {
        return berryPosition;
    }
}
