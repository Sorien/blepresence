package com.sorien.ppbthome;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class AdvertiserService extends Service {

    private static final String TAG = AdvertiserService.class.getSimpleName();

    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private AdvertisingSetCallback mAdvertiseCallback;

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "example.permanence";

    private static AdvertiserService mInstance = null;

    private int mTransmitedCount;

    public static boolean isServiceCreated() {
        try {
            // If instance was not cleared but the service was destroyed an Exception will be thrown
            return mInstance != null && mInstance.ping();
        } catch (NullPointerException e) {
            // destroyed/not-started
            return false;
        }
    }

    @Override
    public void onCreate() {
        startMyOwnForeground();
        initialize();
        startAdvertising();
        super.onCreate();
        mInstance = this;
        mTransmitedCount = 0;
    }

    @Override
    public void onDestroy() {
        stopAdvertising();
        stopForeground(true);
        mInstance = null;
        super.onDestroy();
    }

    private Notification createNotification(String text)
    {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        return notificationBuilder.setOngoing(true)
            .setContentTitle(text)
            .setSmallIcon(R.drawable.ic_stat_bluetooth)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build();
    }

    private void startMyOwnForeground()
    {
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        startForeground(NOTIFICATION_ID, createNotification("Transiting in background"));
    }

    private void notify(String text) {

        Notification notification = createNotification(text);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private boolean ping() {
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initialize() {
        if (mBluetoothLeAdvertiser == null) {
            BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager != null) {
                BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
                if (mBluetoothAdapter != null) {
                    mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
                } else {
                    Toast.makeText(this, getString(R.string.bt_null), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.bt_null), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startAdvertising() {
        Log.d(TAG, "Service: Starting Advertising");

        if (mAdvertiseCallback == null) {
            mAdvertiseCallback = new BleAdvertiseCallback();

            AdvertisingSetParameters parameters = (new AdvertisingSetParameters.Builder())
                    .setLegacyMode(true) // True by default, but set here as a reminder.
                    .setConnectable(false)
                    .setInterval(AdvertisingSetParameters.INTERVAL_HIGH)
                    .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MEDIUM)
                    .build();

            BtHomeAdvertiseDataBuilder serviceDataBuilder = (new BtHomeAdvertiseDataBuilder())
                .AddBinarySensorData(BtHomeBinarySensorId.Pressence, true);

            AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
            dataBuilder.setIncludeDeviceName(true);
            dataBuilder.addServiceData(Constants.Service_UUID, serviceDataBuilder.build());

            if (mBluetoothLeAdvertiser != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mBluetoothLeAdvertiser.startAdvertisingSet(parameters, dataBuilder.build(), null, null, null, mAdvertiseCallback);
            }
        }
    }

    private void stopAdvertising() {
        Log.d(TAG, "Service: Stopping Advertising");
        if (mBluetoothLeAdvertiser != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mBluetoothLeAdvertiser.stopAdvertisingSet(mAdvertiseCallback);
            mAdvertiseCallback = null;
        }
    }

    private static class BleAdvertiseCallback extends AdvertisingSetCallback {
        @Override
        public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
            Log.i(TAG, "onAdvertisingSetStarted(): txPower:" + txPower + " , status: " + status);
        }

        @Override
        public void onPeriodicAdvertisingDataSet(AdvertisingSet advertisingSet, int status) {
            Log.i(TAG, "onPeriodicAdvertisingDataSet(): status: " + status);
        }
    }
}