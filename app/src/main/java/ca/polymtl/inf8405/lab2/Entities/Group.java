package ca.polymtl.inf8405.lab2.Entities;

import java.util.HashMap;

public class Group {
    
    private String name;
    private User organizer;
    // This boolean indicates if 3 places have been selected and are waiting on members' votes
    private boolean isVoting;
    private boolean isVotingFinished;
    private HashMap<String, User> subscribedUsers;
    private HashMap<String, EventLocation> eventLocations;
    
    public Group() {
        
    }
    
    public Group(String name, User organizer, HashMap<String, User> subscribedUsers, HashMap<String, EventLocation> eventLocations, boolean isVoting, boolean isVotingFinished) {
        this.name = name;
        this.organizer = organizer;
        this.subscribedUsers = subscribedUsers;
        this.eventLocations = eventLocations;
        this.isVoting = isVoting;
        this.isVotingFinished = isVotingFinished;
    }
    
    public HashMap<String, EventLocation> getEventLocations() {
        return eventLocations;
    }
    
    public void setEventLocations(HashMap<String, EventLocation> eventLocations) {
        this.eventLocations = eventLocations;
    }
    
    public HashMap<String, User> getSubscribedUsers() {
        return subscribedUsers;
    }
    
    public void setSubscribedUsers(HashMap<String, User> subscribedUsers) {
        this.subscribedUsers = subscribedUsers;
    }
    
    public String getName() {
        return name;
    }

    public boolean isVoting() {
        return isVoting;
    }

    public boolean isVotingFinished() {
        return isVotingFinished;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setIsVoting(boolean voting) {
        this.isVoting = voting;
    }

    public void setIsVotingFinished(boolean voting) {
        this.isVotingFinished = voting;
    }
    
    public User getOrganizer() {
        return organizer;
    }
    
    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }
    
    public void setOrganizerName(User organizer) {
        this.organizer = organizer;
    }
    
    
}
