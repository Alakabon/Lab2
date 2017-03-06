package ca.polymtl.inf8405.lab2.Entities;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

/**
 * Created by Marco on 3/4/2017.
 */

//Entity representing an EventGroup, which regroups all the info the app needs for a specific group

public class EventGroup {
    
    private String groupName;
    private String organizer;
    private ArrayList<EventLocation> eventLocations;
    private ArrayList<User> subscribedUsers;
    
    public EventGroup(String groupName, String organizer, ArrayList<EventLocation> eventLocations, ArrayList<User> subscribedUsers) {
        this.groupName = groupName;
        this.organizer = organizer;
        this.eventLocations = eventLocations;
        this.subscribedUsers = subscribedUsers;
    }
    
    @Exclude
    public String getGroupName() {
        
        return groupName;
    }
    
    @Exclude
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    @Exclude
    public String getOrganizer() {
        return organizer;
    }
    
    @Exclude
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
    
    @Exclude
    public ArrayList<EventLocation> getEventLocations() {
        return eventLocations;
    }
    
    @Exclude
    public void setEventLocations(ArrayList<EventLocation> eventLocations) {
        this.eventLocations = eventLocations;
    }
    
    @Exclude
    public ArrayList<User> getSubscribedUsers() {
        return subscribedUsers;
    }
    
    @Exclude
    public void setSubscribedUsers(ArrayList<User> subscribedUsers) {
        this.subscribedUsers = subscribedUsers;
    }
}
