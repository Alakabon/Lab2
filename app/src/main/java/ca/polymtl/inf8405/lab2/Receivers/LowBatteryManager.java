package ca.polymtl.inf8405.lab2.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import ca.polymtl.inf8405.lab2.Managers.GlobalDataManager;

public class LowBatteryManager extends BroadcastReceiver {
    private static final String TAG = "LowBatteryManager";
    private Context _ctx;

    //___________________________________________________________________________________________________________________________________//
    public LowBatteryManager(Context ctx) {
        _ctx = ctx;
        readCurrentBatteryLevel();
    }

    //___________________________________________________________________________________________________________________________________//
    @Override
    public void onReceive(Context context, Intent intent) {
        readCurrentBatteryLevel();
    }

    //___________________________________________________________________________________________________________________________________//
    private void readCurrentBatteryLevel() {
        try {
            Intent _intent = _ctx.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            GlobalDataManager _gdm = (GlobalDataManager) _ctx.getApplicationContext();
            _gdm.setBatteryLevel(_intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }
}