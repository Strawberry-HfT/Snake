package de.hft.stuttgart.strawberry.snake;

import android.graphics.Point;

/**
 * Created by Tommy_2 on 06.04.2015.
 * Erstellt die Erdbeere als Objekt
 */
public class Strawberry {

    private Point position = new Point();

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
