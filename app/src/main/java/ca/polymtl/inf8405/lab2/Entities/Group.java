package ca.polymtl.inf8405.lab2.Entities;

import java.util.HashMap;

public class Group {
    
    private String name;
    private User organizer;
    // This boolean indicates if 3 places have been selected and are waiting on members' votes
    private boolean voting;
    private boolean voteStarted;
    private boolean sendingAttendance;
    private HashMap<String, User> subscribedUsers = new HashMap<>();
    private HashMap<String, EventLocation> eventLocations = new HashMap<>();
    
    public Group() {
        
    }
    
    public Group(String name, User organizer, HashMap<String, User> subscribedUsers, HashMap<String, EventLocation> eventLocations, boolean voting, boolean voteStarted, boolean sendingAttendance) {
        this.name = name;
        this.organizer = organizer;
        this.subscribedUsers = subscribedUsers;
        this.eventLocations = eventLocations;
        this.voting = voting;
        this.voteStarted = voteStarted;
        this.sendingAttendance = sendingAttendance;
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

    public boolean getVoting() {
        return voting;
    }

    public boolean getVoteStarted() {
        return voteStarted;
    }

    public boolean getSendingAttendance() {
        return sendingAttendance;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setVoting(boolean voting) {
        this.voting = voting;
    }

    public void setVoteStarted(boolean voteStarted) {
        this.voteStarted = voteStarted;
    }

    public void setSendingAttendance(boolean sendingAttendance) {
        this.sendingAttendance = sendingAttendance;
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
