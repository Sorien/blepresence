package com.sorien.ppbthome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = AdvertiserService.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Receive broadcast");

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Config config = new Config(context);
            if (config.getAutoStart()) {
                Log.d(TAG, "Autostarting service");
                context.startService(new Intent(context, AdvertiserService.class));
            }
        }
    }
}
