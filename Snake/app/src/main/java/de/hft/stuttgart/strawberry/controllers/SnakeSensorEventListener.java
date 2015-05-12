package de.hft.stuttgart.strawberry.controllers;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import de.hft.stuttgart.strawberry.common.Movement;

/**
 * Created by Juliano on 02.05.2015.
 */
public class SnakeSensorEventListener implements SensorEventListener {
    private Movement direction;

    public SnakeSensorEventListener(Movement direction) {
        this.direction = direction;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Runter
        if (event.values[0] >= 0 && event.values[0] >= event.values[1] && event.values[0] >= (event.values[1] * -1)) {
            this.direction.setDown(true);
        }

        // Hoch
        if (event.values[0] <= 0 && event.values[0] <= event.values[1] && event.values[0] <= (event.values[1] * -1)) {
            this.direction.setUp(true);
        }

        // Rechts
        if (event.values[1] >= 0 && event.values[1] >= event.values[0] && event.values[1] >= (event.values[0] * -1)) {
            this.direction.setRight(true);
        }

        // Links
        if (event.values[1] <= 0 && event.values[1] <= event.values[0] && event.values[1] <= (event.values[0] * -1)) {
            this.direction.setLeft(true);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nichts
    }
}
