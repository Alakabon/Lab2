package ca.polymtl.inf8405.lab2.Managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GPSManager {
    private static final String TAG = "GPSManager";
    private Context _ctx;
    private Location _loc;

    //___________________________________________________________________________________________________________________________________//
    public GPSManager(Context ctx) {
        _ctx = ctx;
        try {
            LocationManager _lm = (LocationManager) _ctx.getSystemService(Context.LOCATION_SERVICE);
            String _pn = _lm.getBestProvider(new Criteria(), true);
            if (_pn != null)
                _loc = _lm.getLastKnownLocation(_pn);

            _lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new GPSLocationListener());
        } catch (SecurityException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    //___________________________________________________________________________________________________________________________________//
    private class GPSLocationListener implements LocationListener {

        //_______________________________________________________________________________________________________________________________//
        public void onLocationChanged(Location location) {
            if (location != null) {
                GlobalDataManager _gdm = (GlobalDataManager) _ctx.getApplicationContext();
                _gdm.setGPSLatitude(location.getLatitude());
                _gdm.setGPSLongitude(location.getLongitude());
                _loc = location;
            }
        }

        //_______________________________________________________________________________________________________________________________//
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        //_______________________________________________________________________________________________________________________________//
        public void onProviderEnabled(String s) {

        }

        //_______________________________________________________________________________________________________________________________//
        public void onProviderDisabled(String s) {

        }
    }
}