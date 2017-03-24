package ca.polymtl.inf8405.lab2.Managers;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.app.Dialog;
import android.app.AlertDialog;
import android.view.WindowManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.*;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory ;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.polymtl.inf8405.lab2.R;
import ca.polymtl.inf8405.lab2.Activities.MainActivity;
import ca.polymtl.inf8405.lab2.Entities.EventLocation;
import ca.polymtl.inf8405.lab2.Entities.User;

public class MapsManager extends Fragment implements
        OnMapReadyCallback,
        OnInfoWindowClickListener
{
    View _view;
    private GoogleMap _map;
    private ArrayList<Marker> _markerArray = new ArrayList();
    private ArrayList<Marker> _confirmedLocationMarkersArray = new ArrayList();
    private HashMap<String, Integer> _confirmedVotes = new HashMap();
    private GlobalDataManager gdm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Inflate the Fragment's view and call findViewById() on the View to set event handler
        _view = inflater.inflate(R.layout.tab2_map, container, false);
        return _view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        gdm = (GlobalDataManager) this.getActivity().getApplicationContext();
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null)
        {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap)
    {
        _map = googleMap;
        _map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
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
    public void onInfoWindowClick(final Marker marker)
    {
        // Allow the organizer to edit the infos only if the 3 locations havent been sent yet
        if (!_confirmedLocationMarkersArray.contains(marker))
        {
            // Display a dialog so the organizer can edit place infos
            final Dialog dialog = new Dialog(getActivity());

            dialog.setContentView(R.layout.place_information_dialog_window);
            dialog.setTitle("Modifier le lieu");

            final EditText editText = (EditText) dialog.findViewById(R.id.editName);
            Button btnSave = (Button) dialog.findViewById(R.id.save);
            btnSave.setOnClickListener(new OnClickListener()
            {
                public void onClick(View v)
                {
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
            btnCancel.setOnClickListener(new OnClickListener()
            {
                public void onClick(View v)
                {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    public void updatePlacesMarkers()
    {
        updateInfoWindowClickListener();
        if (gdm.get_group_data() != null)
        {
            if (gdm.get_group_data().getVoting() && gdm.get_group_data().getVoteStarted())
            {
                for (EventLocation event : gdm.get_group_data().getEventLocations().values())
                {
                    if (event.getRatings().containsKey(gdm.getUserData().getName()))
                    {
                        addPlaceMarker(_map, event.getGpsLatitude(), event.getGpsLongitude(), event.getLocationName(), Float.toString(event.calculateAverageRating()));
                    }
                    else
                    {
                        addPlaceMarker(_map, event.getGpsLatitude(), event.getGpsLongitude(), event.getLocationName(), "Veuillez voter pour ce lieu");
                    }
                }
            }
            else
            {
                for (EventLocation event : gdm.get_group_data().getEventLocations().values())
                {
                    if (event.getRsvp().containsKey(gdm.getUserData().getName()))
                    {
                        addPlaceMarker(_map, event.getGpsLatitude(), event.getGpsLongitude(), event.getLocationName(), event.getRsvp().toString());
                    }
                    else
                    {
                        addPlaceMarker(_map, event.getGpsLatitude(), event.getGpsLongitude(), event.getLocationName(), "Selectionner un lieu final");
                    }
                }
            }
        }
    }

    private void updateInfoWindowClickListener()
    {
        if (gdm.get_group_data() != null)
        {
            // Votes are starting
            if (gdm.get_group_data().getVoting() && gdm.get_group_data().getVoteStarted())
            {
                _map.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
                {
                    public void onInfoWindowClick(final Marker marker)
                    {
                        // Display a dialog so the organizer can edit place infos
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.setContentView(R.layout.place_vote_dialog_window);
                        dialog.setTitle("Entrer votre vote");

                        final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_vote);

                        List<String> stringList = new ArrayList<>();
                        for(int i=1; i < 6 ; i++) {
                            stringList.add(Integer.toString(i));
                        }

                        for(int i = 0 ; i < stringList.size() ; i++){
                            RadioButton rb = new RadioButton(getActivity()); // dynamically creating RadioButton and adding to RadioGroup.
                            rb.setId(i+1);
                            rb.setText(stringList.get(i));
                            rg.addView(rb);
                        }
                        Button btnSave = (Button) dialog.findViewById(R.id.save);
                        btnSave.setOnClickListener(new OnClickListener()
                        {
                            public void onClick(View v)
                            {
                                int selectedId = rg.getCheckedRadioButtonId();
                                // get selected radio button from radioGroup
                                _confirmedVotes.put(marker.getTitle().toString(), selectedId);
                                marker.setSnippet(marker.getSnippet() + "/n Votre vote:" + selectedId);
                                dialog.dismiss();
                                if (_confirmedVotes.size() == 3)
                                {
                                    sendVotesToServer();
                                }
                            }
                        });
                        Button btnCancel = (Button) dialog.findViewById(R.id.cancel);
                        btnCancel.setOnClickListener(new OnClickListener()
                        {
                            public void onClick(View v)
                            {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
            }
            // Votes are finished, and organizer selects the final location
            else if (gdm.get_group_data().getVoteStarted() && !gdm.get_group_data().getSendingAttendance())
            {
                _map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                    public void onInfoWindowClick(final Marker marker) {
                        // Display a dialog so the organizer can edit place infos
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.setContentView(R.layout.place_final_selection_dialog_window);
                        dialog.setTitle("Sélectionner un lieu final");

                        final EditText editText = (EditText) dialog.findViewById(R.id.editFinalName);
                        final EditText editInfos = (EditText) dialog.findViewById(R.id.editFinalInformations);
                        Button btnSave = (Button) dialog.findViewById(R.id.save);
                        btnSave.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                marker.setTitle(editText.getText().toString());
                                marker.setSnippet(editInfos.getText().toString());
                                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                dialog.dismiss();
                                sendConfirmedLocationToServer(marker);
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
                });
            }
            // Final location is finished, attendance is starting
            else {
                if (gdm.get_group_data().getSendingAttendance()) {
                    _map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                        public void onInfoWindowClick(final Marker marker) {
                            // Display a dialog so the organizer can edit place infos
                            final Dialog dialog = new Dialog(getActivity());

                            dialog.setContentView(R.layout.place_attendance_dialog_window);
                            dialog.setTitle("Entrer votre présence");

                            final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_vote);
                            Button btnSave = (Button) dialog.findViewById(R.id.save);
                            final RadioButton rb1 = new RadioButton(getActivity()); // dynamically creating RadioButton and adding to RadioGroup.
                            rb1.setId(0);
                            rb1.setText("Participe");
                            rg.addView(rb1);
                            final RadioButton rb2 = new RadioButton(getActivity()); // dynamically creating RadioButton and adding to RadioGroup.
                            rb2.setId(1);
                            rb2.setText("Peut-etre");
                            rg.addView(rb2);
                            final RadioButton rb3 = new RadioButton(getActivity()); // dynamically creating RadioButton and adding to RadioGroup.
                            rb3.setId(2);
                            rb3.setText("Ne participe pas");
                            rg.addView(rb3);
                            btnSave.setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {
                                    int selectedId = rg.getCheckedRadioButtonId();
                                    switch (selectedId) {
                                        case 0:
                                            ((MainActivity) getActivity()).getDatabaseManager().setRSVP(gdm.get_group_data().getEventLocations().get(marker.getTitle()), rb1.getText().toString());
                                            break;
                                        case 1:
                                            ((MainActivity) getActivity()).getDatabaseManager().setRSVP(gdm.get_group_data().getEventLocations().get(marker.getTitle()), rb2.getText().toString());
                                            break;
                                        case 2:
                                            ((MainActivity) getActivity()).getDatabaseManager().setRSVP(gdm.get_group_data().getEventLocations().get(marker.getTitle()), rb3.getText().toString());
                                            break;
                                    }
                                    marker.setSnippet(marker.getSnippet() + "/n Votre présence:");
                                    dialog.dismiss();
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
                    });
                }
            }

        }
    }

    public void updatePeopleMarkers()
    {
        if (gdm.get_group_data() != null)
        {
            for (User user : gdm.get_group_data().getSubscribedUsers().values())
            {
                addPersonMarker(_map, user.getGpsLatitude(), user.getGpsLongitude(), user.getName());
            }
        }
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
        gdm.get_group_data().setVoting(true);
        ((MainActivity)getActivity()).getDatabaseManager().setVotingStatus(true);
        gdm.get_group_data().setVoteStarted(true);
        ((MainActivity)getActivity()).getDatabaseManager().setVoteStartedStatus();
    }

    private void sendVotesToServer()
    {
        boolean votingIsOver = false;
        for(Map.Entry<String, Integer> entry : _confirmedVotes.entrySet())
        {
            gdm.get_group_data().getEventLocations().get(entry.getKey()).getRatings().put(gdm.getUserData().getName(), entry.getValue());
            ((MainActivity)getActivity()).getDatabaseManager().rateEventLocation(gdm.get_group_data().getEventLocations().get(entry.getKey()), entry.getValue());
            if (gdm.get_group_data().getEventLocations().get(entry.getKey()).getRatings().size() == gdm.get_group_data().getSubscribedUsers().size() + 1)
            {
                votingIsOver = true;
            }
            else
            {
                votingIsOver = false;
            }
        }
        if (votingIsOver)
        {
            gdm.get_group_data().setVoting(false);
            ((MainActivity)getActivity()).getDatabaseManager().setVotingStatus(false);
        }
    }

    private void sendConfirmedLocationToServer(Marker marker)
    {
        //gdm.get_group_data().getEventLocations().clear();
        gdm.get_group_data().setSendingAttendance(true);
        ((MainActivity)getActivity()).getDatabaseManager().setSendingAttendanceStatus();
        EventLocation event = new EventLocation(gdm.get_group_data().getName(), marker.getTitle(), marker.getPosition().longitude, marker.getPosition().latitude, null, null, true, null, null, marker.getSnippet(), new HashMap<String, String>());
        ((MainActivity)getActivity()).getDatabaseManager().finalizeEventLocations(event);
    }


    private void addPersonMarker(GoogleMap map, double lat, double lon, String name) {
        map.addMarker(new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.person_marker))
                .position(new LatLng(lat, lon))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
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
