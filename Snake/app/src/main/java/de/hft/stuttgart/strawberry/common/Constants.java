package de.hft.stuttgart.strawberry.common;

/**
 * Definiert einige Konstanten zwischen dem BluetoothService und dem UI.
 * Created by Tommy_2 on 13.05.2015.
 */
public class Constants {

    // Nachrichtenarten vom BluetoothService zum Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Empfangene Schlüsselwörter vom BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

}
