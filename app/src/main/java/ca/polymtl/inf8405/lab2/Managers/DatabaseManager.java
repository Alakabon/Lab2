package ca.polymtl.inf8405.lab2.Managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ca.polymtl.inf8405.lab2.Entities.EventLocation;
import ca.polymtl.inf8405.lab2.Entities.Group;
import ca.polymtl.inf8405.lab2.Entities.User;
import ca.polymtl.inf8405.lab2.R;

public class DatabaseManager {
    private static final String TAG = "DatabaseManager";
    private final String rootLabel = "root";
    private final String usersLabel = "users";
    private final String groupsLabel = "groups";
    private final String subscribedUsersLabel = "subscribedUsers";
    private final String eventLocationsLabel = "eventLocations";

    private Context _ctx;
    private Group _group;
    private User _currentUser;
    private boolean _result = false;

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

    public void addEventLocation(EventLocation eventLocation) {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child(rootLabel).child(groupsLabel).child(_currentUser.getGroup());
        DatabaseReference ref = groupRef.child(eventLocationsLabel);
        ref.child(eventLocation.getLocationName()).setValue(eventLocation);
    }

    public void rateEventLocation(EventLocation eventLocation, String userName, float rating) {
        //Coming soon
    }

    public void configureAppDB(boolean enableOfflineStorage) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(enableOfflineStorage);
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

    //___________________________________________________________________________________________________________________________________//
    public boolean saveUserData(final User userData) {
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
    }
}
