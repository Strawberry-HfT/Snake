package de.hft.stuttgart.strawberry.common;

/**
 * Definiert einige Konstanten zwischen dem BluetoothService und dem UI.
 * Created by Tommy_2 on 13.05.2015.
 */
public class Constants {

    // Kennzahlen fuer den Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final int POSITION_READ = 6;
    public static final int LEVEL_SPEED = 7;

    public static final long STAMP_IDEX = 55;


    // Schluesselwoerter fuer den Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String NOTIFIER_SELECTED = "level_selected";
    public static final String NOTIFIER_STARTED = "game_started";

    // Tag fuer Fragment
    public static final String FRAGMENT_TAG = "difficulty_fragment";

    public static final int STREAM_BUFFER_SIZE = 1024;

    // Spielfeldgröße
    public final static int XTILE_COUNT = 34;
    public final static int YTILE_COUNT = 20;

    public final static int SPEED_EASY = 300;
    public final static int SPEED_MEDIUM = 200;
    public final static int SPEED_HARD = 100;




}
