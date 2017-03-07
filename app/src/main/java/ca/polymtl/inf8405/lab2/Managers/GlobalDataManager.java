package ca.polymtl.inf8405.lab2.Managers;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Menu;

import java.util.ArrayList;

import ca.polymtl.inf8405.lab2.Entities.EventLocation;
import ca.polymtl.inf8405.lab2.R;

public class GlobalDataManager extends Application {
    private int _online_status = -1;
    private int _battery_level = -1;
    private int _measurement_scale = -1;
    private Fragment[] _fgms;
    private Context _ctx;
    private Menu _menu;
    private double _gps_Longitude;
    private double _gps_Latitude;
    
    public int getOnlineStatus() {
        return _online_status;
    }
    
    public void setOnlineStatus(int status) {
        _online_status = status;
    }
    
    public CharSequence getOnlineStatusString() {
        return (_online_status == 1) ? "Online" : (_online_status == 0) ? "Offline" : "Error readin status!";
    }
    
    public void updateOnlineStatusOnTheMainActivity() {
        if (_menu != null)
            _menu.getItem(0).setIcon((_online_status == 1) ? R.drawable.online : (_online_status == 0) ? R.drawable.offline : R.drawable.error);
    }
    
    public void updateOnlineStatusOnTheMainActivity(Menu menu) {
        _menu = menu;
        updateOnlineStatusOnTheMainActivity();
    }
    
    public void setTabs(Fragment[] fgms, Context ctx) {
        _fgms = fgms;
        _ctx = ctx;
    }
    
    public CharSequence getBatteryStatusString() {
        return String.valueOf(_battery_level).concat("%");
    }
    
    public void setBatteryLevel(int level) {
        _battery_level = level;
    }
    
    public void setMeasurementScale(int scale) {
        _measurement_scale = scale;
    }
    
    public CharSequence getGPSLatitudeString() {
        return String.valueOf(_gps_Latitude);
    }
    
    public CharSequence getGPSLongitudeString() {
        return String.valueOf(_gps_Longitude);
    }
    
    public double getGPSLatitude() {
        return _gps_Latitude;
    }
    
    public void setGPSLatitude(double latitude) {
        _gps_Latitude = latitude;
    }
    
    public double getGPSLongitude() {
        return _gps_Longitude;
    }
    
    public void setGPSLongitude(double longitude) {
        _gps_Longitude = longitude;
    }
}