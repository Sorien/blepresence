package com.sorien.ppbthome

import android.Manifest
import android.app.Notification
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.AdvertisingSetCallback
import com.sorien.ppbthome.AdvertiserService
import androidx.core.app.NotificationCompat
import com.sorien.ppbthome.R
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothAdapter
import android.widget.Toast
import com.sorien.ppbthome.AdvertiserService.BleAdvertiseCallback
import android.bluetooth.le.AdvertisingSetParameters
import com.sorien.ppbthome.IBeaconAdvertiseDataBuilder
import android.bluetooth.le.AdvertiseData
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.bluetooth.le.AdvertisingSet
import android.graphics.Color
import android.util.Log
import java.lang.NullPointerException
import java.util.*

class AdvertiserService : Service() {
    private var mBluetoothLeAdvertiser: BluetoothLeAdvertiser? = null
    private var mAdvertiseCallback: AdvertisingSetCallback? = null
    override fun onCreate() {
        startMyOwnForeground()
        initialize()
        startAdvertising()
        super.onCreate()
        mInstance = this
    }

    override fun onDestroy() {
        stopAdvertising()
        stopForeground(true)
        mInstance = null
        super.onDestroy()
    }

    private fun createNotification(text: String): Notification {
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        return notificationBuilder.setOngoing(true)
                .setContentTitle(text)
                .setSmallIcon(R.drawable.ic_stat_bluetooth)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
    }

    private fun startMyOwnForeground() {
        val channelName = "Background Service"
        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        startForeground(NOTIFICATION_ID, createNotification("Transiting in background"))
    }

    private fun notify(text: String) {
        val notification = createNotification(text)
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun ping(): Boolean {
        return true
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun initialize() {
        if (mBluetoothLeAdvertiser == null) {
            val mBluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            if (mBluetoothManager != null) {
                val mBluetoothAdapter = mBluetoothManager.adapter
                if (mBluetoothAdapter != null) {
                    mBluetoothLeAdvertiser = mBluetoothAdapter.bluetoothLeAdvertiser
                } else {
                    Toast.makeText(this, getString(R.string.bt_null), Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.bt_null), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startAdvertising() {
        Log.d(TAG, "Service: Starting Advertising")
        if (mAdvertiseCallback == null) {
            mAdvertiseCallback = BleAdvertiseCallback()
            val parameters = AdvertisingSetParameters.Builder()
                    .setLegacyMode(true)
                    .setScannable(true)
                    .setConnectable(false)
                    .setInterval(AdvertisingSetParameters.INTERVAL_HIGH)
                    .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MEDIUM)
                    .build()
            val dataBuilder = IBeaconAdvertiseDataBuilder(
                    UUID.fromString("580774c2-ad48-4a83-881b-2af77324aa53"), 100.toShort(), 0.toShort(), (-50.toByte()).toByte())
            val scanResponse = AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .build()
            if (mBluetoothLeAdvertiser != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                mBluetoothLeAdvertiser!!.startAdvertisingSet(parameters, dataBuilder.build(), scanResponse, null, null, mAdvertiseCallback)
            }
        }
    }

    private fun stopAdvertising() {
        Log.d(TAG, "Service: Stopping Advertising")
        if (mBluetoothLeAdvertiser != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            mBluetoothLeAdvertiser!!.stopAdvertisingSet(mAdvertiseCallback)
            mAdvertiseCallback = null
        }
    }

    private class BleAdvertiseCallback : AdvertisingSetCallback() {
        override fun onAdvertisingSetStarted(advertisingSet: AdvertisingSet, txPower: Int, status: Int) {
            Log.i(TAG, "onAdvertisingSetStarted(): txPower:$txPower , status: $status")
        }

        override fun onPeriodicAdvertisingDataSet(advertisingSet: AdvertisingSet, status: Int) {
            Log.i(TAG, "onPeriodicAdvertisingDataSet(): status: $status")
        }
    }

    companion object {
        private val TAG = AdvertiserService::class.java.simpleName
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "example.permanence"
        private var mInstance: AdvertiserService? = null// destroyed/not-started

        // If instance was not cleared but the service was destroyed an Exception will be thrown
        val isServiceCreated: Boolean
            get() = try {
                // If instance was not cleared but the service was destroyed an Exception will be thrown
                mInstance != null && mInstance!!.ping()
            } catch (e: NullPointerException) {
                // destroyed/not-started
                false
            }
    }
}