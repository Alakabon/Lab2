package ca.polymtl.inf8405.lab2.Entities;

import com.google.firebase.database.Exclude;

/**
 * Created by Marco on 3/4/2017.
 */

public class User {
    private String name;
    private String group;
    private String photo_location;

    public User(String name, String group) {
        this.name = name;
        this.group = group;
    }

    @Exclude
    public String getName() {
        return name;
    }
    @Exclude
    public String getGroup() {
        return group;
    }
    @Exclude
    public void setName(String name) {

        this.name = name;
    }
    @Exclude
    public void setGroup(String group) {
        this.group = group;
    }
}
