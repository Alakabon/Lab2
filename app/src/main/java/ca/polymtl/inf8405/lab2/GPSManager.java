package ca.polymtl.inf8405.lab2;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

class GPSManager {
    private Context _ctx;

    private Location _loc;

    GPSManager(Context context) {
        _ctx = context;
        try {
            LocationManager _lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            String _pn = _lm.getBestProvider(new Criteria(), true);
            if (_pn != null)
                _loc = _lm.getLastKnownLocation(_pn);

            _lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new GPSLocationListener());
        } catch (SecurityException ex) {
        }
    }


    private class GPSLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            if (location != null) {
                GlobalDataManager _gdm = (GlobalDataManager) _ctx.getApplicationContext();
                _gdm.setGPSLatitude(location.getLatitude());
                _gdm.setGPSLongitude(location.getLongitude());
                _loc = location;
            }
//        if
        }

        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        public void onProviderEnabled(String s) {

        }

        public void onProviderDisabled(String s) {

        }
    }
}