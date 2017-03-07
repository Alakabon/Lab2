package ca.polymtl.inf8405.lab2.Activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

import ca.polymtl.inf8405.lab2.Entities.EventLocation;
import ca.polymtl.inf8405.lab2.Entities.User;
import ca.polymtl.inf8405.lab2.Managers.DatabaseManager;
import ca.polymtl.inf8405.lab2.Managers.EventsManager;
import ca.polymtl.inf8405.lab2.Managers.GPSManager;
import ca.polymtl.inf8405.lab2.Managers.GlobalDataManager;
import ca.polymtl.inf8405.lab2.Managers.LowBatteryManager;
import ca.polymtl.inf8405.lab2.Managers.MapsManager;
import ca.polymtl.inf8405.lab2.Managers.NetworkManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        

        /* BroadcastReceiver registration section
           In Android these are the components that listen to broadcast events.
           Broadcast events are events that are sent with the intention of notifying multiple receivers.
           Android uses these broadcasts to inform interested components of system events, like
           application installs, mounting or removing the sd card, a low battery, the completion of the boot process and so on.
           In this section we will register two BroadcastReceiver for detecting low battery level and connectivity status changes
           First command will register battery receiver for detecting low battery level
           Second command will fire an event whenever connectivity status changes (We can check the device’s current connectivity status.
                                                                                   But this is only a temporary snapshot of the status.
                                                                                   It might change anytime, given the volatility of a mobile environment.
                                                                                   Thus we will register for a broadcast event.)
         */
        registerReceiver(new LowBatteryManager(this), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(new NetworkManager(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        GPSManager _gps = new GPSManager(this);
        final DatabaseManager _dbManager = new DatabaseManager(this,null,null); // Params subject to change
        _dbManager.configureAppDB(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        Fragment[] _fgms = {new ProfileManager(), new MapsManager(), new PlaceManager(), new EventsManager(), new StatusManager()};
        ((GlobalDataManager) this.getApplicationContext()).setTabs(_fgms, this);

        // The ViewPager that will host the section contents and setting up the ViewPager with the sections adapter.
        ViewPager _vp = (ViewPager) findViewById(R.id.vpContainer);
        _vp.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), this, _fgms));

        TabLayout _tab = (TabLayout) findViewById(R.id.tabs);
        _tab.setupWithViewPager(_vp);
        _tab.getTabAt(0).setIcon(R.drawable.profile_icon);
        _tab.getTabAt(1).setIcon(R.drawable.google_maps_icon);
        _tab.getTabAt(2).setIcon(R.drawable.places_icon);
        _tab.getTabAt(3).setIcon(R.drawable.events_icon);
        _tab.getTabAt(4).setIcon(R.drawable.status_icon);

        //Profile saving
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.msg_save), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                
                // Make user and link to DB
                EditText nameField = (EditText) findViewById(R.id.txt_alias);
                EditText groupField = (EditText) findViewById(R.id.txt_group);
                
                User currentUser = new User(nameField.getText().toString(),groupField.getText().toString(),"", 0,0 );
                
                _dbManager.set_currentUser(currentUser);
                _dbManager.login();
                
            }
        });
        
        FloatingActionButton fabDebugAdd = (FloatingActionButton) findViewById(R.id.fab_add__debug_event);
        fabDebugAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameField = (EditText) findViewById(R.id.txt_alias);
                EditText groupField = (EditText) findViewById(R.id.txt_group);
    
                User currentUser = new User(nameField.getText().toString(),groupField.getText().toString(),"", 0,0 );
                
                EventLocation eventLocation = new EventLocation(currentUser.getGroup(),"debugLocation",0,0,null,"no_photo",false, Calendar.getInstance().getTime(),Calendar.getInstance().getTime(),"no info", null);
                _dbManager.addEventLocation(eventLocation);
            }
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        NetworkManager.isOnline(this);
        ((GlobalDataManager) this.getApplicationContext()).updateOnlineStatusOnTheMainActivity(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_status) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
