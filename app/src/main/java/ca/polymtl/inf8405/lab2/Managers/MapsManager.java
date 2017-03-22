package ca.polymtl.inf8405.lab2.Managers;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.app.Dialog;
import android.app.AlertDialog;
import android.view.WindowManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.*;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.polymtl.inf8405.lab2.R;
import ca.polymtl.inf8405.lab2.Activities.MainActivity;
import ca.polymtl.inf8405.lab2.Entities.EventLocation;

public class MapsManager extends Fragment implements
        OnMapReadyCallback,
        OnInfoWindowClickListener {
    View _view;
    private GoogleMap _map;
    private ArrayList<Marker> _markerArray = new ArrayList<Marker>();
    private ArrayList<Marker> _confirmedLocationMarkersArray = new ArrayList<Marker>();
    private GlobalDataManager gdm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the Fragment's view and call findViewById() on the View to set event handler
        _view = inflater.inflate(R.layout.tab2_map, container, false);
        return _view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        gdm = (GlobalDataManager) this.getActivity().getApplicationContext();
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        _map = googleMap;
        _map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
                // TODO check if the user is the organizer and the EventLocation state (isVoting, chosen)
                addPlaceMarker(_map, point.latitude, point.longitude, "Nom", "Photo");
            }
        });
        _map.setOnInfoWindowClickListener(this);
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(gdm.getGPSLatitude(),
                        gdm.getGPSLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        addPersonMarker(_map, gdm.getGPSLatitude(), gdm.getGPSLongitude(), gdm.getUserData().getName());
        _map.moveCamera(center);
        _map.animateCamera(zoom);
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        // TODO - modifify information windows based on EventLocation state (basic state = infos, isVoting = infos + vote input, chosen - infos + attendance input)
        // Allow the organizer to edit the infos only if the place hasnt been confirmed yet
        if (!_confirmedLocationMarkersArray.contains(marker)) {
            // Display a dialog so the organizer can edit place infos
            final Dialog dialog = new Dialog(getActivity());

            dialog.setContentView(R.layout.place_information_dialog_window);
            dialog.setTitle("Modifier le lieu");

            final EditText editText = (EditText) dialog.findViewById(R.id.editName);
            Button btnSave = (Button) dialog.findViewById(R.id.save);
            btnSave.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    // TODO update the marker without having to reclick on it
                    // TODO change the marker icon to show it's been validated
                    // TODO add picture support
                    marker.setTitle(editText.getText().toString());
                    _confirmedLocationMarkersArray.add(marker);
                    dialog.dismiss();
                    if (_confirmedLocationMarkersArray.size() == 3)
                    {
                        createEventLocations();
                    }
                }
            });
            Button btnCancel = (Button) dialog.findViewById(R.id.cancel);
            btnCancel.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    public void updateMarkers() {
        // TODO - modifify markers based on EventLocation state (basic state, isVoting, chosen)
        /*if (gdm.get_group_data().isVoting())
        {
            if (!gdm.get_group_data().getEventLocations().containsKey(gdm.getUserData().getName()))
            {
                for (EventLocation event : gdm.get_group_data().getEventLocations().values()) {
                addPlaceMarker(_map, event.getGpsLatitude(), event.getGpsLongitude(), event.getLocationName(), "Veuillez voter pour ce lieu");
                }
            }
            else
            {

            }
        }
        else if (gdm.get_group_data().isVotingFinished())
        {
            /*for (EventLocation event : gdm.get_group_data().getEventLocations().values()) {
            addPlaceMarker(_map, event.getGpsLatitude(), event.getGpsLongitude(), event.getLocationName(), event.calculateAverageRating());
            }*/
        //}
        //else
        //{

        //}
        /*for (User user : gdm.get_group_data().getSubscribedUsers().values()) {
            addPersonMarker(_map, user.getGpsLatitude(), user.getGpsLongitude(), user.getName());
        }*/
    }

    private void createEventLocations()
    {
        // Create a new EventLocation for every confirmed marker on the map
        for(Marker marker : _confirmedLocationMarkersArray)
        {
            EventLocation event = new EventLocation(gdm.get_group_data().getName(), marker.getTitle(), marker.getPosition().longitude, marker.getPosition().latitude, "");
            ((MainActivity)getActivity()).getDatabaseManager().addEventLocation(event);
        }
        // Enables the voting status of the group
        gdm.get_group_data().setIsVoting(true);
    }


    private void addPersonMarker(GoogleMap map, double lat, double lon, String name) {
        map.addMarker(new MarkerOptions()
                // TODO add a customized icon for Person markers
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.person_marker))
                .position(new LatLng(lat, lon))
                .title(name));
    }

    private void addPlaceMarker(GoogleMap map, double lat, double lon, String title, String snippet) {
        // If less than 3 place markers have been added to the map
        if (_markerArray.size() < 3)
        {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title(title)
                    .snippet(snippet));
            _markerArray.add(marker);
        }
    }
}
