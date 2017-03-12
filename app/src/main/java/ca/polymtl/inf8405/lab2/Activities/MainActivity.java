package ca.polymtl.inf8405.lab2.Activities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ca.polymtl.inf8405.lab2.Entities.EventLocation;
import ca.polymtl.inf8405.lab2.Entities.User;
import ca.polymtl.inf8405.lab2.Managers.DatabaseManager;
import ca.polymtl.inf8405.lab2.Managers.EventsManager;
import ca.polymtl.inf8405.lab2.Receivers.GPSManager;
import ca.polymtl.inf8405.lab2.Managers.GlobalDataManager;
import ca.polymtl.inf8405.lab2.Receivers.LowBatteryManager;
import ca.polymtl.inf8405.lab2.Managers.MapsManager;
import ca.polymtl.inf8405.lab2.Receivers.NetworkManager;
import ca.polymtl.inf8405.lab2.Managers.PlaceManager;
import ca.polymtl.inf8405.lab2.Managers.ProfileManager;
import ca.polymtl.inf8405.lab2.Managers.SectionsPagerAdapter;
import ca.polymtl.inf8405.lab2.Managers.StatusManager;
import ca.polymtl.inf8405.lab2.R;

public class MainActivity extends AppCompatActivity {
    /**
     * The android.support.v4.view.PagerAdapter that will provide
     * fragments for each of the sections. We use a
     * FragmentPagerAdapter derivative, which will keep every
     * loaded fragment in memory.
     */

    private static final String TAG = "MainActivity";
    private User _localProfile;
    private SharedPreferences _sharedPref;

    //___________________________________________________________________________________________________________________________________//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _sharedPref = this.getSharedPreferences("PREF_DATA", Context.MODE_PRIVATE);
        readSavedLocalProfile();
        final GlobalDataManager _gdm = (GlobalDataManager) this.getApplicationContext();
        _gdm.setUserData(_localProfile);

        /* BroadcastReceiver registration section

           In Android these are the components that listen to broadcast events.
           Broadcast events are events that are sent with the intention of notifying multiple receivers.
           Android uses these broadcasts to inform interested components of system events, like
           application installs, mounting or removing the sd card, a low battery, the completion of the boot process and so on.
           In this section we will register two BroadcastReceiver for detecting low battery level and connectivity status changes
           First command will register battery receiver for detecting low battery level
           There are two ways of registering broadcast receivers in Android:
                1- In the code (our case)
                2- In AndroidManifest.xml
           Second command will fire an event whenever connectivity status changes (We can check the device’s current connectivity status.
                                                                                   But this is only a temporary snapshot of the status.
                                                                                   It might change anytime, given the volatility of a mobile environment.
                                                                                   Thus we will register for a broadcast event.)

            We will only receive broadcasts as long as context where we registered your receiver is alive.
            So when activity or application is killed (depending where we registered your receiver) we won't receive broadcasts anymore.
         */

        //
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        //Register broadcast receiver for the application context
        registerReceiver(new LowBatteryManager(this), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(new NetworkManager(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(new GPSManager(this), new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        final DatabaseManager _dbManager = new DatabaseManager(this, null, null); // Params subject to change
        _dbManager.configureAppDB(true);

        // Create the adapter that will return a fragment for each of primary sections of the activity.
        final Fragment[] _fgms = {new ProfileManager(), new MapsManager(), new PlaceManager(), new EventsManager(), new StatusManager()};
        _gdm.setTabs(_fgms, this);

        // The ViewPager that will host the section contents and setting up the ViewPager with the sections adapter.
        final ViewPager _vp = (ViewPager) findViewById(R.id.vpContainer);
        _vp.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), this, _fgms));

        TabLayout _tab = (TabLayout) findViewById(R.id.tabs);
        _tab.setupWithViewPager(_vp);
        _tab.getTabAt(0).setIcon(R.drawable.profile_icon);
        _tab.getTabAt(1).setIcon(R.drawable.google_maps_icon);
        _tab.getTabAt(2).setIcon(R.drawable.places_icon);
        _tab.getTabAt(3).setIcon(R.drawable.events_icon);
        _tab.getTabAt(4).setIcon(R.drawable.status_icon);

        //Preparing and Saving data to DB
        ((FloatingActionButton) findViewById(R.id.fab_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    switch (_vp.getCurrentItem()) {
                        case 0: //Save profile data from the 1st Fragment (Profile Manager)
                            _gdm.getUserData().setName(((EditText) findViewById(R.id.txt_alias)).getText().toString());
                            _gdm.getUserData().setGroup(((EditText) findViewById(R.id.txt_group)).getText().toString());
                            _gdm.getUserData().setPhoto_url(((ImageView) findViewById(R.id.img_profile)).getTag().toString());
                            break;
                        case 1: //Save map data from the 2st Fragment (Map Manager)
                            break;
                        case 2: //Save place data from the 3st Fragment (Place Manager)
                            break;
                        case 3: //Save event data from the 4st Fragment (Events Manager)
                            break;
                        case 4: //Save status data from the 5st Fragment (Status Manager)
                            _gdm.getUserData().setGpsLatitude(Double.valueOf(((TextView) findViewById(R.id.txt3)).getText().toString()));
                            _gdm.getUserData().setGpsLongitude(Double.valueOf(((TextView) findViewById(R.id.txt4)).getText().toString()));
                            break;
                    }
                    _dbManager.saveUserData(_gdm.getUserData());
                    applySavedLocalProfile();
                    Snackbar.make(view, getString(R.string.msg_save) + "[" + _vp.getCurrentItem() + "]", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    // Make user and link to DB
//                    EditText nameField = (EditText) findViewById(R.id.txt_alias);
//                    EditText groupField = (EditText) findViewById(R.id.txt_group);
//
//                    User currentUser = new User(nameField.getText().toString(), groupField.getText().toString(), "", 0, 0);
//
//                    _dbManager.set_currentUser(currentUser);
//                    _dbManager.login();
                } catch (Exception ex) {
                    Snackbar.make(view, "ERROR:" + ex.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

        FloatingActionButton fabDebugAdd = (FloatingActionButton) findViewById(R.id.fab_add__debug_event);
        fabDebugAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameField = (EditText) findViewById(R.id.txt_alias);
                EditText groupField = (EditText) findViewById(R.id.txt_group);

                User currentUser = new User(nameField.getText().toString(), groupField.getText().toString(), "", 0, 0);

                EventLocation eventLocation = new EventLocation(currentUser.getGroup(), "debugLocation", 0, 0, null, "no_photo", false, Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), "no info", null);
                _dbManager.addEventLocation(eventLocation);
            }
        });
    }

    //___________________________________________________________________________________________________________________________________//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        ((GlobalDataManager) this.getApplicationContext()).updateOnlineStatusOnTheMainActivity(menu);
        ((GlobalDataManager) this.getApplicationContext()).updateGPSProviderStatusOnTheMainActivity(menu);
        return true;
    }

    //___________________________________________________________________________________________________________________________________//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        String _status = "ERROR reading data";
        try {
            switch (item.getItemId()) {
                case R.id.menu_network:
                    _status = "Internet status : " + ((GlobalDataManager) this.getApplicationContext()).getOnlineStatusString();
                    break;
                case R.id.menu_gps:
                    _status = "GPS status : " + ((GlobalDataManager) this.getApplicationContext()).getGPSProviderStatusString();
                    break;
            }

            Toast.makeText(this, _status, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return super.onOptionsItemSelected(item);
    }

    //___________________________________________________________________________________________________________________________________//
    // Reading saved preferences (e.g., Last username) for loading proper user object from Firebase
    // If it was empty, strip out the username from the GMail address
    // Otherwise, will create a random username
    private String getLastUsedUsername() {
        String _username = _sharedPref.getString(getString(R.string.name_alias), "");
        if (_username.isEmpty()) {
            List<String> _emails = new LinkedList<String>();
            for (Account account : AccountManager.get(this).getAccountsByType("com.google"))
                _emails.add(account.name);

            if (!_emails.isEmpty() && _emails.get(0) != null) {
                String[] parts = _emails.get(0).split("@");
                if (parts.length > 1) return parts[0];
            }

            return "User".concat(String.valueOf(new Random().nextInt(2000 - 1000) + 1000));
        }
        return _username;
    }

    //___________________________________________________________________________________________________________________________________//
    private void readSavedLocalProfile() {
        _localProfile = new User();
        _localProfile.setName(getLastUsedUsername());
        String _last_group = _sharedPref.getString(getString(R.string.name_group), "");
        _localProfile.setGroup(_last_group.isEmpty() ? "Group".concat(String.valueOf(new Random().nextInt(90 - 10) + 10)) : _last_group);
        _localProfile.setGpsLatitude(_sharedPref.getFloat(getString(R.string.lbl_Latitude), 0));
        _localProfile.setGpsLongitude(_sharedPref.getFloat(getString(R.string.lbl_Longitude), 0));
        ByteArrayOutputStream _stream = new ByteArrayOutputStream();
        Bitmap _photo = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
        _photo.compress(Bitmap.CompressFormat.PNG, 100, _stream);
        _localProfile.setPhoto_url(_sharedPref.getString(getString(R.string.camera), Base64.encodeToString(_stream.toByteArray(), Base64.DEFAULT)));
    }

    //___________________________________________________________________________________________________________________________________//
    private void applySavedLocalProfile() {
        GlobalDataManager _gdm = (GlobalDataManager) this.getApplicationContext();
        User _currentProfile = _gdm.getUserData();
        SharedPreferences.Editor _editor = _sharedPref.edit();
        _editor.putString(getString(R.string.name_alias), _currentProfile.getName());
        _editor.putString(getString(R.string.name_group), _currentProfile.getGroup());
        _editor.putFloat(getString(R.string.lbl_Latitude), Float.valueOf(String.valueOf(_currentProfile.getGpsLatitude())));
        _editor.putFloat(getString(R.string.lbl_Longitude), Float.valueOf(String.valueOf(_currentProfile.getGpsLongitude())));
        _editor.putString(getString(R.string.camera), _currentProfile.getPhoto_url());
        _editor.apply();
    }

    //___________________________________________________________________________________________________________________________________//
    // Using SharedPreferences to store preferences of the application when application is closing
    @Override
    public void onStop() {
        super.onStop();
        applySavedLocalProfile();
    }
}
