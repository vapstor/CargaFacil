package br.com.cargafacil.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

public class DevicesActivity extends Activity {

    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean DEBUG = true;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private boolean foundPrinter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

        if (mBtAdapter == null) {
            // Get the local Bluetooth adapter
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        foundPrinter = false;
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("BlueTooth Printer")) {
                    mBtAdapter.cancelDiscovery();
                    foundPrinter = true;
                    backToPesagemWithDevice(device);
                }
            }
        }
        if (!foundPrinter) {
            doDiscovery();
        }
    }

//
//    private void dialogDispositivosNaoPareados() {
//        new MaterialAlertDialogBuilder(this, R.style.customMaterialDialogLight)
//                .setTitle("Impressora não pareada")
//                .setMessage("Seu smartphone e sua impressora necessitam estar pareados para imprimir. Por favor, execute esta ação.")
//                .setPositiveButton("Ok", (dialog, which) -> {
////                    Log.d(MY_LOG_TAG, "Tela de CONFIGURAÇÕES para pareamento (onActivityResult")
//                    finish();
//                })
////                    .setNegativeButton("Cancelar", null)
//                .show();
//    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (DEBUG) Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
//        setProgressBarIndeterminateVisibility(true);
        Toast.makeText(this, "Buscando Impressora...", Toast.LENGTH_SHORT).show();

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        mBtAdapter.startDiscovery();
    }

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
//                    if (device.getName().equals("BlueTooth Printer")) {
                    if (device.getName().equals("nave-mae")) {
                        mBtAdapter.cancelDiscovery();
                        //foundPrinter!
                        foundPrinter = true;
                        backToPesagemWithDevice(device);
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (!foundPrinter) {
                    Toast.makeText(context, "Impressora não encontrada", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    private void backToPesagemWithDevice(BluetoothDevice device) {
        // Get the device MAC address
        String address = device.getAddress();
        // Create the result Intent and include the MAC address
        Intent backIntent = new Intent();
        backIntent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, backIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }
}
