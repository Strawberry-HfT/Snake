package de.hft.stuttgart.strawberry.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import de.hft.stuttgart.strawberry.snake.R;

/**
 * Diese Klasse listet Bluetooth-Geraete auf und verwaltet
 * die Paarung fuer den Multiplayer.
 */
public class BluetoothSearchActivity extends Activity {

    // TAG fuer den Logger
    private static final String TAG = BluetoothSearchActivity.class.getSimpleName();

    // Konstante fuer die MAC Adresse, die mitgegeben wird
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";

    // BT Adapter
    private BluetoothAdapter mBTAdapter;

    // ArrayAdapter fuer gepaarte Geraete
    private ArrayAdapter<String> pairedDevicesAdapter;

    // ArrayAdapter fuer nicht gepaarte Geraete
    private ArrayAdapter<String> newDevicesAdapter;

    // Button zum Scannen
    private Button btnScan;

    /*
    Standard onCreate Methode
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_bluetooth_search);

        setResult(Activity.RESULT_CANCELED);

        initWidgets();

        registerReceiviers();

        // Adapter setzen
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        getPairedDevices();
    }

    @Override
    protected void onStart() {
        super.onStart();
        findDevices();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();

        // Suche beenden
        if (mBTAdapter != null) {
            mBTAdapter.cancelDiscovery();
        }

        // Empfaenger deaktivieren
        this.unregisterReceiver(myReceiver);

        // Ergebnis setzen und diese Activity beenden
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    /*
    Bei beenden soll die Suche gestoppt und der Empf�nger deaktiviert werden
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Suche beenden
        if (mBTAdapter != null) {
            mBTAdapter.cancelDiscovery();
        }

        // Empfaenger deaktivieren
        this.unregisterReceiver(myReceiver);
    }

    /*
    Sucht nach Geraeten in Reichweite
    */
    private void findDevices() {
        // TODO die setter hier funktionieren noch nicht, mach ich noch
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Liste aus letzter Suche leeren
        newDevicesAdapter.clear();

        // Button ausblenden
        btnScan.setVisibility(View.GONE);

        // Titel einblenden
        findViewById(R.id.header_new_devices).setVisibility(View.VISIBLE);

        // Falls eine Suche durchgefuehrt wird, soll diese beendet werden
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }

        // Suche starten
        mBTAdapter.startDiscovery();
        Log.d(TAG, "startDiscovery()");
    }

    /*
    ClickListener fuer alle in der Liste aufgefuehrten Geraete
     */
    private AdapterView.OnItemClickListener selectDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            // Suche abbrechen, da wir uns gerade verbinden m�chten
            mBTAdapter.cancelDiscovery();

            // Die MAC Adresse holen, diese sind die letzten 17 Zeichen der View
            String info = ((TextView) v).getText().toString();//TODO die MAC wird aus dem Text der ListView geholt. Sieht unschön aus Sollte eher aus einem Set geholt werden dann muss nur der Name in der Liste angezeigt werden
            String address = info.substring(info.length() - 17);

            // Intent erzeugen und MAC Adresse mitgeben
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Ergebnis setzen und diese Activity beenden
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    /*
    Empfaenger fuer gefundene Geraete
     */
    final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // gefundenes Ger�t in Objekt �bergeben
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // nur in die Liste neuer Ger�te setzten, falls die noch nicht gepaart wurden
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    newDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                    Log.d(TAG, "Found new Device: " + device.getName());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //TODO funktioniert noch nicht, mach ich noch
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);

                // Button einblenden
                btnScan.setVisibility(View.VISIBLE);

                if (newDevicesAdapter.getCount() == 0) {
                    newDevicesAdapter.add(getResources().getText(R.string.no_devices).toString());
                }
            }
        }
    };

    /*
    initialisiert die Widgets dieser Activity
     */
    private void initWidgets() {

        // Such-Button initialisieren
        btnScan = (Button) findViewById(R.id.btn_scan);

        // Button-ClickListener
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBTAdapter != null && !mBTAdapter.isDiscovering()) {
                    findDevices();
                }
            }
        });

        // Adapter fuer die ListViews initialisieren
        pairedDevicesAdapter = new ArrayAdapter<>(this, R.layout.tv_device_name);
        newDevicesAdapter = new ArrayAdapter<>(this, R.layout.tv_device_name);

        // ListView fuer gepaarte Geraete initialisieren
        ListView pairedListView = (ListView) findViewById(R.id.lv_paired_devices);
        pairedListView.setAdapter(pairedDevicesAdapter);
        pairedListView.setOnItemClickListener(selectDeviceClickListener);

        // ListView fuer neue Geraete initialisieren
        ListView newDevicesListView = (ListView) findViewById(R.id.lv_new_devices);
        newDevicesListView.setAdapter(newDevicesAdapter);
        newDevicesListView.setOnItemClickListener(selectDeviceClickListener);
    }

    /*
    Registriert die Empfaenger
     */
    private void registerReceiviers() {

        // Registriert Empfaenger wenn ein Geraet gefunden wurde
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(myReceiver, filter);

        // Registriert Empfaenger wenn Suche beendet wurde
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(myReceiver, filter);
    }

    /*
    holt alle bereits gepaarten Geraete fuer die ListView
     */
    private void getPairedDevices() {

        // Liste mit bereits gepaarten Ger�ten sammeln
        Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Titel einblenden
            findViewById(R.id.header_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                Log.d(TAG, "Found Paired Device: " + device.getName());
            }
        } else {
            pairedDevicesAdapter.add(getString(R.string.no_devices));
            Log.d(TAG, getString(R.string.no_devices));
        }
    }

}
