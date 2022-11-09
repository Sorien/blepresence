package com.sorien.ppbthome

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sorien.ppbthome.BootReceiver
import com.sorien.ppbthome.AdvertiserService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.v(TAG, "Receive broadcast")
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val config = Config(context)
            if (config.autoStart) {
                Log.d(TAG, "Autostarting service")
                context.startService(Intent(context, AdvertiserService::class.java))
            }
        }
    }

    companion object {
        private val TAG = AdvertiserService::class.java.simpleName
    }
}