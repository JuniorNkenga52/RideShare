package com.app.rideWhiz.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.app.rideWhiz.utils.PrefUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BatteryInfoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
        int icon_small = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, 0);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
        boolean present = intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
        String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
        int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);


      /*  Log.w("",
                "Health: " + health + "\n" +
                        "Icon Small:" + icon_small + "\n" +
                        "Level: " + level + "\n" +
                        "Plugged: " + plugged + "\n" +
                        "Present: " + present + "\n" +
                        "Scale: " + scale + "\n" +
                        "Status: " + status + "\n" +
                        "Technology: " + technology + "\n" +
                        "Temperature: " + temperature + "\n" +
                        "Voltage: " + voltage + "\n");*/

        Intent ii = new Intent("ContentResponse");
        ii.putExtra("level", level);
        LocalBroadcastManager.getInstance(context).sendBroadcast(ii);
        PrefUtils.putString(PrefUtils.PREF_BATTERY_LEVEL, String.valueOf(level));
    }

}