package ca.polymtl.inf8405.lab2.Receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import ca.polymtl.inf8405.lab2.Managers.GlobalDataManager;

public class GPSManager extends BroadcastReceiver {
    private static final String TAG = "GPSManager";
    private Context _ctx;
    private Location _loc;

    //___________________________________________________________________________________________________________________________________//
    public void onReceive(Context context, Intent intent) {
        updateGPSProviderStatus(context);
    }

    //___________________________________________________________________________________________________________________________________//
    private void updateGPSProviderStatus(Context ctx)
    {
        GlobalDataManager _gdm = (GlobalDataManager) ctx.getApplicationContext();
        _gdm.setGPSProviderStatus(isGPSFunctional(ctx));
        _gdm.updateGPSProviderStatusOnTheMainActivity();
    }

    //___________________________________________________________________________________________________________________________________//
    // This method will check functionality of the gps provider (if it's on/off).
    public static int isGPSFunctional(Context ctx) {
        try {
            return ((LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE )).isProviderEnabled(LocationManager.GPS_PROVIDER) ? 1 : 0;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return -1;
        }
    }

    //___________________________________________________________________________________________________________________________________//
    public GPSManager(Context ctx) {
        _ctx = ctx;
        try {
            LocationManager _lm = (LocationManager) _ctx.getSystemService(Context.LOCATION_SERVICE);
            Criteria _crt = new Criteria();
            _crt.setAccuracy(Criteria.ACCURACY_MEDIUM);
            _crt.setPowerRequirement(Criteria.POWER_LOW);
            String _pn = _lm.getBestProvider(_crt, true);
            if (_pn != null)
                _loc = _lm.getLastKnownLocation(_pn);

            //Checking if there is still permission for accessing GPS location
            if (_ctx.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED)
            {
                int _battery_level = ((GlobalDataManager) ctx.getApplicationContext()).getBatteryLevel();

                //Minimum time interval between location updates, in milliseconds
                //If in low battery mode, will change internal to 1 minutes instead of 10 second
                int _minTime = _battery_level <= 15 ? 10000 : 60000;
                _lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, _minTime, 0, new GPSLocationListener());
            }
        } catch (SecurityException ex) {
            Log.e(TAG, ex.getMessage());
        }
        updateGPSProviderStatus(ctx);
    }

    //___________________________________________________________________________________________________________________________________//
    private class GPSLocationListener implements LocationListener {

        //_______________________________________________________________________________________________________________________________//
        @Override
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