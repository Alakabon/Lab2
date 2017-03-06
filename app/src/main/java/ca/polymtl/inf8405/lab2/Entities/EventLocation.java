package ca.polymtl.inf8405.lab2.Entities;

import android.location.Location;
import android.util.Log;
import com.google.firebase.database.Exclude;
import java.util.Map;

/**
 * Created by Marco on 3/4/2017.
 */


public class EventLocation {
    @Exclude
    private final String TAG= "EventLocationEntity";

    private String locationName;
    private Location location;  // Could also split in 2 floats but a lot of the methods on location look very useful for distances
    private Map<String,Float> ratings;       // (UserName, Rating)
    private String photo_location;

    public EventLocation(){

    }

    public EventLocation(String locationName, Location location,Map<String,Float> ratings) {
        this.locationName = locationName;
        this.location = location;
        this.ratings = ratings;
    }

    @Exclude
    public Map<String,Float> getRatings() {
        return ratings;
    }

    @Exclude
    public void setRatings(Map<String,Float> ratings) {
        this.ratings = ratings;
    }
    @Exclude
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    @Exclude
    public void setLocation(Location location) {
        this.location = location;
    }

    @Exclude
    public String getLocationName() {

        return locationName;
    }
    @Exclude
    public Location getLocation() {
        return location;
    }

    @Exclude
    private float calculateAverageRating(){
        float ratingCount=0;
        float sumRatings=0;
        float currentRating=0;

        for (String s : ratings.keySet()) {
            // Make sure rating is not null;
            currentRating =ratings.get(s);
            if (!ratings.get(s).equals(null)) {
                sumRatings += currentRating;
                ratingCount++;
            }
            else{
                Log.d(TAG, "A null value was inserted in place of a rating, please be careful");
                return 0;
            }
        }
        if (ratingCount > 0){
            return sumRatings/ratingCount;
        }
        else{
            return 0;
        }
    }
}
