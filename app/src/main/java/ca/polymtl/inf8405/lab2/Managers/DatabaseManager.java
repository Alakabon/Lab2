package ca.polymtl.inf8405.lab2.Managers;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ca.polymtl.inf8405.lab2.Entities.EventLocation;
import ca.polymtl.inf8405.lab2.Entities.Group;
import ca.polymtl.inf8405.lab2.Entities.User;
import ca.polymtl.inf8405.lab2.Receivers.GPSManager;

public class DatabaseManager {
    private static final String TAG = "DatabaseManager";
    
    //Labels
    public final String ratingsLabel = "ratings";
    public final String rsvpLabel = "rsvp";
    private final String rootLabel = "root";
    private final String usersLabel = "users";
    private final String groupsLabel = "groups";
    private final String subscribedUsersLabel = "subscribedUsers";
    private final String eventLocationsLabel = "eventLocations";
    private final String organizerLabel = "organizer";
    private final String gpsLatiduteLabel = "gpsLatitude";
    private final String gpsLongitudeLabel = "gpsLongitude";
    private final String votingLabel = "voting";
    private final String voteStartedLabel = "voteStarted";
    private final String sendingAttendanceLabel = "sendingAttendance";
    
    private Context _ctx;
    private boolean _isLoggedIn = false;
    private boolean _result = false;
    
    public DatabaseManager(Context ctx) {
        _ctx = ctx;
    }
    
    public boolean get_isLoggedIn() {
        return _isLoggedIn;
    }
    
    public void set_isLoggedIn(boolean _isLoggedIn) {
        this._isLoggedIn = _isLoggedIn;
    }
    
    public void login() {
        
        //Get group and verify if it exists and if user is registered
        final String userGroup = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData().getGroup();
        final String userName = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData().getName();
        
        FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(userGroup).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Group already exists, load it up for app to use
                if (dataSnapshot.exists()) {
                    Group _group = dataSnapshot.getValue(Group.class);
                    ((GlobalDataManager) _ctx.getApplicationContext()).set_group_data(_group);
                    //Initial GPS update
                    GPSManager.getLatestGPSLocation(_ctx);
                    Location loc = ((GlobalDataManager) _ctx.getApplicationContext()).getGPSLocation();
                    updateUserLocation(loc.getLongitude(), loc.getLatitude());

                    // TODO - crashes if the user tries to join the group before locations are added, or on first group create
                    //If user name is not organizer, add him to group if he's not in map
                    if (!_group.getOrganizer().getName().equals(userName)) {
                        addUserToExistingGroup();
                    }
                }
                
                // Group does not exist, create new group and make user organizer.
                else {
                    createNewGroup();
                }
                _isLoggedIn = true;
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, "Login", databaseError.toException());
            }
        });
    }
    
    public void syncGroupData() {
        final String userGroup = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData().getGroup();
        final String userName = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData().getName();
        
        // Get group ref
        FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(userGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Group exists, load it up for app to use
                if (dataSnapshot.exists()) {
                    Group _group = dataSnapshot.getValue(Group.class);
                    ((GlobalDataManager) _ctx.getApplicationContext()).set_group_data(_group);
                }
                ((GlobalDataManager) _ctx.getApplicationContext()).getMapsManager().updatePlacesMarkers();
                ((GlobalDataManager) _ctx.getApplicationContext()).getMapsManager().updatePeopleMarkers();
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, "Sync", databaseError.toException());
            }
        });
    }
    
    private void createNewGroup() {
        final User user = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();
        Group group = new Group(user.getGroup(), user, new HashMap<String, User>(), new HashMap<String, EventLocation>(), false, false, false, false);
        FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(user.getGroup()).setValue(group);
    }
    
    private void addUserToExistingGroup() {
        final Group group = ((GlobalDataManager) _ctx.getApplicationContext()).get_group_data();
        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();
        
        // Not organiser, No list yet
        if (group.getSubscribedUsers() == null) {
            HashMap<String, User> newSubscriberMap = new HashMap<>();
            group.setSubscribedUsers(newSubscriberMap);
        }
        // Not organiser, Not found in subscribed users
        if (group.getSubscribedUsers().get(currentUser.getName()) == null) {
            group.getSubscribedUsers().put(currentUser.getName(), currentUser);
            FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(currentUser.getGroup()).getRef().child(subscribedUsersLabel).setValue(group.getSubscribedUsers());
        }
    }
    
    public void removeUserFromGroup(){
    
        final Group group = ((GlobalDataManager) _ctx.getApplicationContext()).get_group_data();
        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();
        
        DatabaseReference userRef = getCurrentUserRef();
        String locationName;
        //Organiser, promote someone else or do nothing?
        if (group.getOrganizer().getName().equals(currentUser.getName())) {
            Toast.makeText(_ctx,"L'organisateur de peut pas se desinscrire du groupe", Toast.LENGTH_SHORT);
        }
        //Regular user, wipe related data from rsvp and and rating
        else {
            if (userRef != null) {
                HashMap eventLocations = group.getEventLocations();
                Iterator it = eventLocations.entrySet().iterator();
                // remove rating and rsvp
                while (it.hasNext()){
                    Map.Entry pair = (Map.Entry)it.next();
                    EventLocation location = ((EventLocation)pair.getValue());
                    locationName = location.getLocationName();
    
                    DatabaseReference specificEventLocation = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(group.getName()).child(eventLocationsLabel).child(locationName);
                    if (specificEventLocation != null){
                        DatabaseReference specificRatings = specificEventLocation.child(ratingsLabel).child(currentUser.getName());
                        if (specificRatings != null){
                            specificRatings.removeValue();
                        }
                        DatabaseReference specificRSVP =specificEventLocation.child(ratingsLabel).child(currentUser.getName());
                        if(specificRSVP != null){
                            specificRSVP.removeValue();
                        }
                    }
                }
                //Then erase user
                userRef.removeValue();
            }
        }
    }
    
    public void addEventLocation(EventLocation eventLocation) {
        
        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();
        
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(currentUser.getGroup());
        DatabaseReference ref = groupRef.child(eventLocationsLabel);
        ref.child(eventLocation.getLocationName()).setValue(eventLocation);
    }

    public void finalizeEventLocations(EventLocation finalEventLocation) {

        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(currentUser.getGroup());
        DatabaseReference ref = groupRef.child(eventLocationsLabel);
        // TODO - disable/remove old locations
        /*for(EventLocation event : ((GlobalDataManager) _ctx.getApplicationContext()).get_group_data().getEventLocations().values())
        {
            ref.child(event.getLocationName()).removeValue();
        }*/
        ref.child(finalEventLocation.getLocationName()).setValue(finalEventLocation);
    }

    public void setVotingStatus(boolean voting) {

        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(currentUser.getGroup());
        groupRef.child(votingLabel).setValue(voting);
    }

    public void setVoteStartedStatus() {

        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(currentUser.getGroup());
        groupRef.child(voteStartedLabel).setValue(true);
    }

    /*public void sendConfirmedLocationToServer(EventLocation location) {

        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(currentUser.getGroup());
        groupRef.child(eventLocationsLabel).setValue(true);
    }*/

    public void setSendingAttendanceStatus() {

        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(currentUser.getGroup());
        groupRef.child(sendingAttendanceLabel).setValue(true);
    }
    
    public void rateEventLocation(EventLocation eventLocation, int rating) {
        
        final Group group = ((GlobalDataManager) _ctx.getApplicationContext()).get_group_data();
        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();
        
        DatabaseReference specificEvent = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(group.getName()).child(eventLocationsLabel).child(eventLocation.getLocationName());
        specificEvent.child(ratingsLabel).child(currentUser.getName()).setValue(rating);
    }
    
    public void setRSVP(EventLocation eventLocation, String rsvp) {
        
        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();
        DatabaseReference specificEvent = getSpecificEventRef(eventLocation.getLocationName());
        
        specificEvent.child(rsvpLabel).child(currentUser.getName()).setValue(rsvp);
    }
    
    public void updateUserLocation(double longitude, double latitude) {
        
        final Group group = ((GlobalDataManager) _ctx.getApplicationContext()).get_group_data();
        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();
        
        DatabaseReference userRef = getCurrentUserRef();
        
        if (userRef != null) {
            userRef.child(gpsLatiduteLabel).setValue(latitude);
            userRef.child(gpsLongitudeLabel).setValue(longitude);
        }
    }
    
    public void configureAppDB(boolean enableOfflineStorage) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(enableOfflineStorage);
    }
    
    
    public Context get_ctx() {
        return _ctx;
    }
    
    public void set_ctx(Context _ctx) {
        this._ctx = _ctx;
    }


    // TODO: No longer used, remove ?
    //___________________________________________________________________________________________________________________________________//
    /*public boolean saveUserData(final User userData) {
        _result = false;
        try {
            //Creating an instance of DatabaseReference for reading/writing Data from Firebase
            final DatabaseReference _ref = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(usersLabel).child(userData.getName());
            //Attaching an asynchronous listener to reference
            //The listener is triggered once for the initial state of the data and then does not trigger again.
            _ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Since we are using Java object, the contents of the object are automatically mapped to child locations in a nested fashion
                    //HashMap<String, User> _user = new HashMap<String, User>();
                    //_user.put(userData.getName(), userData);
                    _ref.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            _result = task.isSuccessful();
                        }
                    });
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadData:onCancelled", databaseError.toException());
                    _result = false;
                }
            });
            return _result;
        } catch (Exception ex) {
            return false;
        }
    }*/
    
    private DatabaseReference getCurrentUserRef() {
        final Group group = ((GlobalDataManager) _ctx.getApplicationContext()).get_group_data();
        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();
        
        try {
            DatabaseReference specificGroup = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(currentUser.getGroup());
            
            // Make sure specificGroup is a valid ref
            if (specificGroup != null) {
                // Find user  locations ref, depending on organizer or regular user
                if (group.getOrganizer().getName().equals(currentUser.getName())) {
                    return specificGroup.child(organizerLabel);
                } else {
                    return specificGroup.child(subscribedUsersLabel).child(currentUser.getName());
                }
            }
        } catch (Exception e) {
            Log.v(TAG, "Could not locate current user");
            e.printStackTrace();
        }
        return null;
    }
    
    private DatabaseReference getSpecificEventRef(String eventLocationName) {
        final Group group = ((GlobalDataManager) _ctx.getApplicationContext()).get_group_data();
        final User currentUser = ((GlobalDataManager) _ctx.getApplicationContext()).getUserData();
        
        try {
            DatabaseReference specificGroup = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(currentUser.getGroup());
            // Make sure specificGroup is a valid ref
            if (specificGroup != null) {
                return specificGroup.child(eventLocationsLabel).child(eventLocationName);
            }
            
        } catch (Exception e) {
            Log.v(TAG, "Could not find event location " + eventLocationName);
            e.printStackTrace();
        }
        return null;
    }
}
