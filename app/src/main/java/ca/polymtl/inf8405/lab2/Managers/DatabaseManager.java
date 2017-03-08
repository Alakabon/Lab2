package ca.polymtl.inf8405.lab2.Managers;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ca.polymtl.inf8405.lab2.Entities.EventLocation;
import ca.polymtl.inf8405.lab2.Entities.Group;
import ca.polymtl.inf8405.lab2.Entities.User;
import ca.polymtl.inf8405.lab2.R;


public class DatabaseManager {
    public static final String TAG = "DatabaseManager";
    public final String rootLabel = "root";
    public final String groupsLabel = "groups";
    public final String subscribedUsersLabel = "subscribedUsers";
    public final String eventLocationsLabel = "eventLocations";
    
    private Context _ctx;
    private Group _group;
    private User _currentUser;
    
    public DatabaseManager(Context ctx, Group eventGroup, User user) {
        _ctx = ctx;
        _group = eventGroup;
        _currentUser = user;
    }
    
   
    
    public void login() {
        //Get group and verify if it exists and if user is registered
        
        FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(_currentUser.getGroup()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Group already exists, load it up for app to use
                if (dataSnapshot.exists()) {
                    _group = dataSnapshot.getValue(Group.class);
                    
                    //If user name is not organizer, add him to group if he's not in map
                    if (!_group.getOrganizer().getName().equals(_currentUser.getName())) {
                        addUserToExistingGroup();
                    }
                }
                
                // Else, create new group and make user organizer. Also add user to users
                else {
                    createNewGroup();
                }
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG + "/Login", _ctx.getString(R.string.err_DBM_login_failed));
            }
        });
    }
    
    private void createNewGroup() {
        _group = new Group(_currentUser.getGroup(), _currentUser, new HashMap<String, User>(), new HashMap<String, EventLocation>());
        FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(_currentUser.getGroup()).setValue(_group);
    }
    
    private void addUserToExistingGroup() {
        // Not organiser, No list yet
        if (_group.getSubscribedUsers() == null) {
            HashMap<String, User> newSubscriberMap = new HashMap<>();
            _group.setSubscribedUsers(newSubscriberMap);
        }
        if (_group.getSubscribedUsers().get(_currentUser.getName()) == null) {
            _group.getSubscribedUsers().put(_currentUser.getName(), _currentUser);
            FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(_currentUser.getGroup()).getRef().child(subscribedUsersLabel).setValue(_group.getSubscribedUsers());
        }
    }
    
    public void addEventLocation( EventLocation eventLocation){
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(_currentUser.getGroup());
        DatabaseReference ref =  groupRef.child(eventLocationsLabel);
        ref.child(eventLocation.getLocationName()).setValue(eventLocation);
    }
    
    public void rateEventLocation(EventLocation eventLocation, String userName, float rating) {
        //Coming soon
    }
    
    public void configureAppDB(boolean enableOfflineStorage) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(enableOfflineStorage);
    }

    public boolean saveUserData(User userData) {
        try {
            //ToDo: Save each field in the DB
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }

    public User retriveUserData(String userName) {
        try {
            //ToDo: Read user data from DB based on userName and return the user object
            return new User();
        }catch (Exception ex){
            return new User();
        }
    }

    public Group get_group() {
        return _group;
    }
    
    public void set_group(Group _group) {
        this._group = _group;
    }
    
    public Context get_ctx() {
        return _ctx;
    }
    
    public void set_ctx(Context _ctx) {
        this._ctx = _ctx;
    }
    
    public Group get_eventGroup() {
        return _group;
    }
    
    public void set_eventGroup(Group _eventGroup) {
        this._group = _eventGroup;
    }
    
    public User get_currentUser() {
        return _currentUser;
    }
    
    public void set_currentUser(User _currentUser) {
        this._currentUser = _currentUser;
    }
}
