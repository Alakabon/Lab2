package ca.polymtl.inf8405.lab2.Managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class LowBatteryManager extends BroadcastReceiver {

    private Context _ctx;

    public LowBatteryManager(Context ctx) {
        _ctx = ctx;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        GlobalDataManager _gdm = (GlobalDataManager) context.getApplicationContext();
        _gdm.setBatteryLevel(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
        _gdm.setMeasurementScale(intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
    }

    public int getCurrentBatteryLevel() {
        try {
            Intent _intent = _ctx.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            return _intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        } catch (Exception ex) {
            return -1;
        }
    }

    public CharSequence getCurrentBatteryLevelString() {
        return String.valueOf(getCurrentBatteryLevel()).concat("%");
    }
}