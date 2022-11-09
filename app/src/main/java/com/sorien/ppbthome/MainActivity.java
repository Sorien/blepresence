package com.sorien.ppbthome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwitchCompat onOffSwitch = findViewById(R.id.activate_switch_id);

        onOffSwitch.setChecked(AdvertiserService.isServiceCreated());

        onOffSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startServiceRequestPermissions();
            } else {
                stopService();
            }
        });
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
        stopService(new Intent(MainActivity.this, AdvertiserService.class));

        Config config = new Config(this);
        config.setAutoStart(false);

        Toast.makeText(this, "Broadcasting finished.", Toast.LENGTH_SHORT).show();
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

}