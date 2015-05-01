package de.hft.stuttgart.strawberry.snake;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

/**
 * Created by Tommy_2 on 06.04.2015.
 * Erstellt die Erdbeere als Objekt
 */
public class Strawberry {

    // Positon der Beere
    private Point berryPosition = new Point();

    // Displaygröße
    private Point displaySize;

    // Bitmap Bild
    private Bitmap berryBitmap;

    // Constructor
    public Strawberry(){
        this.displaySize = displaySize;
        createBerryPosition();
    }

    // Zeichnen
    public void drawStrawberry(Canvas canvas, Bitmap berryBitmap){
        canvas.drawBitmap(berryBitmap,berryPosition.x, berryPosition.y, null);
    }

    private void createBerryPosition(){
        int berryX;
        int berryY;

        // Zufallspunkte
        berryX = (int) (Math.random() * 50) * 38;
        berryY = (int) (Math.random() * 50) * 18;

        // Wenn Beere am Rand, dann neu erstellen lassen
        if(berryX >= displaySize.x || berryY >= displaySize.y){
            createBerryPosition();
        }

        // Übergibt die lokalen Werte in die globale Position
        berryPosition.x = berryX;
        berryPosition.y = berryY;

        // TODO Prüfen ob Beere auf Schlange erstellt wird
    }

    // Setter und Getter
    public Point getBerryPosition() {
        return berryPosition;
    }

    public void setBerryPosition(Point berryPosition) {
        this.berryPosition = berryPosition;
    }

    public Point getDisplaySize() {
        return displaySize;
    }

    public void setDisplaySize(Point displaySize) {
        this.displaySize = displaySize;
    }

    public Bitmap getBerryBitmap() {
        return berryBitmap;
    }

    public void setBerryBitmap(Bitmap berryBitmap) {
        this.berryBitmap = berryBitmap;
    }
}
