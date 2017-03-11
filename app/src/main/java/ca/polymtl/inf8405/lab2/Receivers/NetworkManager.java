package ca.polymtl.inf8405.lab2.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ca.polymtl.inf8405.lab2.Managers.GlobalDataManager;

public class NetworkManager extends BroadcastReceiver {

    //___________________________________________________________________________________________________________________________________//
    //This method is called whenever the broadcast is sent.
    @Override
    public void onReceive(Context context, Intent intent) {
        GlobalDataManager _gdm = (GlobalDataManager) context.getApplicationContext();
        _gdm.setOnlineStatus(isOnline(context));
        _gdm.updateOnlineStatusOnTheMainActivity();
    }

    //___________________________________________________________________________________________________________________________________//
    // This method will check connectivity in case if we need to check whether mobile is connected with the internet or not.
    public static int isOnline(Context context) {
        try {
            ConnectivityManager _cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo _netInfo = _cm.getActiveNetworkInfo();

            //Whereas in airplane mode it will be null, we should consider null as well
            return (_netInfo != null && _netInfo.isConnected() && _netInfo.isAvailable()) ? 1 : 0;
        }
        catch (Exception ex){
            return -1;
        }
    }
}
