package ca.polymtl.inf8405.lab2;

import android.content.Intent;
import android.content.IntentFilter;
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

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        Fragment[] _fgms = {new ProfileManager(), new MapsManager(), new PlaceManager(), new EventsManager(), new StatusManager()};
        ((GlobalDataManager) this.getApplicationContext()).setTabs(_fgms, this);

        // The ViewPager that will host the section contents and seting up the ViewPager with the sections adapter.
        ViewPager _vp = (ViewPager) findViewById(R.id.vpContainer);
        _vp.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), this, _fgms));

        TabLayout _tab = (TabLayout) findViewById(R.id.tabs);
        _tab.setupWithViewPager(_vp);
        _tab.getTabAt(0).setIcon(R.drawable.profile_icon);
        _tab.getTabAt(1).setIcon(R.drawable.google_maps_icon);
        _tab.getTabAt(2).setIcon(R.drawable.places_icon);
        _tab.getTabAt(3).setIcon(R.drawable.events_icon);
        _tab.getTabAt(4).setIcon(R.drawable.status_icon);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.msg_save), Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
