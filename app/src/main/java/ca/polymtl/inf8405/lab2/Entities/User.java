package ca.polymtl.inf8405.lab2.Entities;

import android.graphics.Bitmap;

import ca.polymtl.inf8405.lab2.Managers.ImageManager;

public class User {
    private String name;
    private String group;
    private String photo_url;
    private double gpsLongitude;
    private double gpsLatitude;
    
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    
    public User(String name, String group, String photo_url, double gpsLongitude, double gpsLatitude) {
        this.name = name;
        this.group = group;
        this.photo_url = photo_url;
        this.gpsLongitude = gpsLongitude;
        this.gpsLatitude = gpsLatitude;
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
    
    public String getPhoto_url() {
        return photo_url;
    }
    
    public void setPhotoURLFromBitmap(Bitmap bitmap) {
        this.photo_url = ImageManager.encodeImageToString(bitmap);
    }
    
    public Bitmap getPhoto_Bitmap() {
        return ImageManager.decodeImageFromString(this.photo_url);
    }
    
    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getGroup() {
        return group;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }
}
