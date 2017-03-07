package ca.polymtl.inf8405.lab2.Entities;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;


public class EventLocation {
    
    private final String TAG = "EventLocationEntity";
    private String groupName;
    private String locationName;
    private double gpsLongitude;
    private double gpsLatitude;
    private HashMap<String, Float> ratings;       // (UserName, Rating)
    private String photo_url;
    private boolean chosen;
    private Date startTime;
    private Date endTime;
    private String information;
    private HashMap<String, String> rsvp; //(User, RSVP) RSVP statuses are stored in strings.xml with prefix rsvp
    
    public EventLocation() {
        
    }
    
    public EventLocation(String groupName, String locationName, double gpsLongitude, double gpsLatitude, HashMap<String, Float> ratings, String photo_url, boolean chosen, Date startTime, Date endTime, String information, HashMap<String, String> rsvp) {
        this.groupName = groupName;
        this.locationName = locationName;
        this.gpsLongitude = gpsLongitude;
        this.gpsLatitude = gpsLatitude;
        this.ratings = ratings;
        this.photo_url = photo_url;
        this.chosen = chosen;
        this.startTime = startTime;
        this.endTime = endTime;
        this.information = information;
        this.rsvp = rsvp;
    }
    
    public boolean isChosen() {
        return chosen;
    }
    
    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }
    
    public Date getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public Date getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    
    public String getInformation() {
        return information;
    }
    
    public void setInformation(String information) {
        this.information = information;
    }
    
    public HashMap<String, String> getRsvp() {
        return rsvp;
    }
    
    public void setRsvp(HashMap<String, String> rsvp) {
        this.rsvp = rsvp;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public String getPhoto_url() {
        return photo_url;
    }
    
    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
    
    public HashMap<String, Float> getRatings() {
        return ratings;
    }
    
    public void setRatings(HashMap<String, Float> ratings) {
        this.ratings = ratings;
    }
    
    public String getLocationName() {
        
        return locationName;
    }
    
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    
    public double getGpsLongitude() {
        
        return gpsLongitude;
    }
    
    public void setGpsLongitude(double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }
    
    public double getGpsLatitude() {
        return gpsLatitude;
    }
    
    public void setGpsLatitude(double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }
    
    @Exclude
    private float calculateAverageRating() {
        float ratingCount = 0;
        float sumRatings = 0;
        float currentRating = 0;
        
        for (String s : ratings.keySet()) {
            // Make sure rating is not null;
            currentRating = ratings.get(s);
            if (!ratings.get(s).equals(null)) {
                sumRatings += currentRating;
                ratingCount++;
            } else {
                Log.d(TAG, "A null value was inserted in place of a rating, please be careful");
                return 0;
            }
        }
        if (ratingCount > 0) {
            return sumRatings / ratingCount;
        } else {
            return 0;
        }
    }
}
