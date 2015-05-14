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
 * Diese Klasse listet Bluetooth-Geräte auf und verwaltet
 * die Paarung für den Multiplayer.
 */
public class BluetoothSearchActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 2;

    private static final String TAG = BluetoothSearchActivity.class.getSimpleName();

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothAdapter myBTAdapter;

    private ArrayAdapter<String> pairedDevicesAdapter;

    private ArrayAdapter<String> newDevicesAdapter;

    private Button btnScan;

    /*
    Standard onCreate Methode
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_bluetooth);

        setResult(Activity.RESULT_CANCELED);

        // Such-Button initialisieren
        btnScan = (Button) findViewById(R.id.btn_scan);

        // Button-ClickListener
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDevices();
            }
        });

        // Adapter für die ListViews initialisieren
        pairedDevicesAdapter = new ArrayAdapter<>(this, R.layout.tv_device_name);
        newDevicesAdapter = new ArrayAdapter<>(this, R.layout.tv_device_name);

        // ListView für gepaarte Geräte initialisieren
        ListView pairedListView = (ListView) findViewById(R.id.lv_paired_devices);
        pairedListView.setAdapter(pairedDevicesAdapter);
        pairedListView.setOnItemClickListener(selectDeviceClickListener);

        // ListView für neue Geräte initialisieren
        ListView newDevicesListView = (ListView) findViewById(R.id.lv_new_devices);
        newDevicesListView.setAdapter(newDevicesAdapter);
        newDevicesListView.setOnItemClickListener(selectDeviceClickListener);

        // Registriert Empfänger wenn ein Gerät gefunden wurde
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(myReceiver, filter);

        // Registriert Empfänger wenn Suche beendet wurde
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(myReceiver, filter);

        // Adapter für die App holen
        myBTAdapter = BluetoothAdapter.getDefaultAdapter();

        // Meldung falls das Gerät kein Bluetooth unterstützt
        if (myBTAdapter == null) {
            Toast.makeText(this, "Bluetooth ist nicht verfügbar", Toast.LENGTH_LONG).show();
        }

        // Bluetooth aktivieren lassen, falls nicht aktiviert
        if (!myBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Liste mit bereits gepaarten Geräten sammeln
        Set<BluetoothDevice> pairedDevices = myBTAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Titel einblenden
            findViewById(R.id.header_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName());
                Log.d(TAG, "Found Paired Device: " + device.getName());
            }
        } else {
            pairedDevicesAdapter.add(getResources().getText(R.string.no_devices).toString());
            Log.d(TAG, getResources().getText(R.string.no_devices).toString());
        }
    }

    /*
    Sucht nach Geräten in Reichweite
     */
    private void findDevices() {
        // TODO funktioniert noch nicht, mach ich noch
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Liste aus letzter Suche leeren
        newDevicesAdapter.clear();

        // Button ausblenden
        btnScan.setVisibility(View.GONE);

        // Titel einblenden
        findViewById(R.id.header_new_devices).setVisibility(View.VISIBLE);

        // Falls eine Suche durchgeführt wird, soll diese beendet werden
        if (myBTAdapter.isDiscovering()) {
            myBTAdapter.cancelDiscovery();
        }

        // Suche starten
        myBTAdapter.startDiscovery();
        Log.d(TAG, "startDiscovery()");
    }

    /*
    ClickListener für alle in der Liste aufgeführten Geräte
     */
    private AdapterView.OnItemClickListener selectDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            // Suche abbrechen, da wir uns gerade verbinden möchten
            myBTAdapter.cancelDiscovery();

            // Die MAC Adresse holen, diese sind die letzten 17 Zeichen der View
            String info = ((TextView) v).getText().toString();
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
    Bei beenden soll die Suche gestoppt und der Empfänger deaktiviert werden
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Suche beenden
        if (myBTAdapter != null) {
            myBTAdapter.cancelDiscovery();
        }

        // Empfänger deaktivieren
        this.unregisterReceiver(myReceiver);
    }

    /*
    Empfänger für gefundene Geräte
     */
    final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // gefundenes Gerät in Objekt übergeben
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // nur in die Liste neuer Geräte setzten, falls die noch nicht gepaart wurden
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

}
