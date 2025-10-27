package com.sorien.ppbthome;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int BLUETOOTH_REQUEST_ENABLE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwitchCompat onOffSwitch = findViewById(R.id.activate_switch_id);

        onOffSwitch.setChecked(AdvertiserService.isServiceCreated());

        onOffSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startServiceEnableBluetooth();
            } else {
                stopService();
            }
        });
    }

    public void startServiceEnableBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager)MainActivity.this.getSystemService(MainActivity.BLUETOOTH_SERVICE);
        if (!bluetoothManager.getAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ActivityCompat.startActivityForResult(MainActivity.this, enableBtIntent, BLUETOOTH_REQUEST_ENABLE, null);
        } else {
            startServiceRequestPermissions();
        }
    }

    public void startServiceRequestPermissions() {

        String[] permissions = new String[]{Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT};
        ArrayList<String> requirePermissions = new ArrayList<>();

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                requirePermissions.add(permission);
            }
        }

        if (!requirePermissions.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this, requirePermissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            startService();
        }
    }

    public void startService() {
        startService(new Intent(MainActivity.this, AdvertiserService.class));

        Config config = new Config(this);
        config.setAutoStart(true);

        Toast.makeText(this, "Broadcasting started.", Toast.LENGTH_SHORT).show();
    }

    public void stopService() {
        if (AdvertiserService.isServiceCreated()) {
            stopService(new Intent(MainActivity.this, AdvertiserService.class));

            Config config = new Config(this);
            config.setAutoStart(false);

            Toast.makeText(this, "Broadcasting finished.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService();
            } else {
                SwitchCompat onOffSwitch = findViewById(R.id.activate_switch_id);
                onOffSwitch.setChecked(AdvertiserService.isServiceCreated());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_REQUEST_ENABLE){
            if (resultCode == RESULT_OK){
                startServiceRequestPermissions();
            }
            else if (resultCode==RESULT_CANCELED){
                SwitchCompat onOffSwitch = findViewById(R.id.activate_switch_id);
                onOffSwitch.setChecked(false);
                Toast.makeText(this, "Bluetooth not enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}