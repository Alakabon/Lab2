package ca.polymtl.inf8405.lab2.Managers;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Menu;

import java.util.ArrayList;

import ca.polymtl.inf8405.lab2.Entities.EventLocation;
import ca.polymtl.inf8405.lab2.Entities.User;
import ca.polymtl.inf8405.lab2.R;

public class GlobalDataManager extends Application {
    private int _online_status = -1;
    private int _gps_provider_status = -1;
    private int _battery_level = -1;
    private Fragment[] _fgms;
    private Context _ctx;
    private Menu _menu;
    private double _gps_Longitude;
    private double _gps_Latitude;
    private User _user_data;

    public int getOnlineStatus() {
        return _online_status;
    }

    public int getGPSProviderStatus() {
        return _gps_provider_status;
    }

    public void setOnlineStatus(int status) {
        _online_status = status;
    }

    public void setGPSProviderStatus(int status) {
        _gps_provider_status = status;
    }

    public CharSequence getGPSProviderStatusString() {
        return (_gps_provider_status == 1) ? "On" : (_gps_provider_status == 0) ? "Off" : "Error reading GPS data!";
    }

    public CharSequence getOnlineStatusString() {
        return (_online_status == 1) ? "Online" : (_online_status == 0) ? "Offline" : "Error reading Internet status!";
    }

    public void updateOnlineStatusOnTheMainActivity() {
        if (_menu != null)
            _menu.getItem(1).setIcon((_online_status == 1) ? R.drawable.online : (_online_status == 0) ? R.drawable.offline : R.drawable.error);
    }

    public void updateGPSProviderStatusOnTheMainActivity() {
        if (_menu != null)
            _menu.getItem(0).setIcon((_gps_provider_status == 1) ? R.drawable.on : (_gps_provider_status == 0) ? R.drawable.off : R.drawable.error);
    }

    public void updateOnlineStatusOnTheMainActivity(Menu menu) {
        _menu = menu;
        updateOnlineStatusOnTheMainActivity();
    }

    public void updateGPSProviderStatusOnTheMainActivity(Menu menu) {
        _menu = menu;
        updateGPSProviderStatusOnTheMainActivity();
    }

    public void setTabs(Fragment[] fgms, Context ctx) {
        _fgms = fgms;
        _ctx = ctx;
    }

    public CharSequence getBatteryLevelString() {
        return String.valueOf(_battery_level).concat("%");
    }

    public int getBatteryLevel() {
        return _battery_level;
    }

    public void setBatteryLevel(int level) {
        _battery_level = level;
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

    public User getUserData() {
        return _user_data;
    }

    public void setUserData(User userData) {
        _user_data = userData;
    }

    public void setPhoto_URL(String photoString) {
        _user_data.setPhoto_url(photoString);
    }
}
