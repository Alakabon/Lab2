package ca.polymtl.inf8405.lab2.Entities;

import android.provider.MediaStore;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.Map;

/**
 * Created by Marco on 3/5/2017.
 */

public class AcceptedEventLocation extends EventLocation{
    @Exclude
    private final String TAG= "EventLocationEntity";

    private Date startTime;
    private Date endTime;
    private String information;
    private Map<String,String> rsvp; //(User, RSVP) RSVP statuses are stored in strings.xml with prefix rsvp


}
