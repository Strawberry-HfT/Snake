package de.hft.stuttgart.strawberry.snake;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Tommy_2 on 26.03.2015.
 * Erstellt die Schlange als Objekt.
 */
public class Snake {

    private ArrayList<Point> position = new ArrayList<Point>();

    public ArrayList<Point> getPosition() {
        return position;
    }

    public void setPosition(ArrayList<Point> position) {
        this.position = position;
    }
}
