package com.example.driveguard.objects;

import android.location.Location;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import trip_data.Event;

@Getter
@Setter
public class Trip implements Serializable {

    private int id;
    private int driverId;
    private String startTime;
    private String endTime;
    private Location startLocation;
    private Location endLocation;
    private TripStatus status;
    private int score;
    private int distance;
    private List<Event> drivingEvents;

    public Trip(int id, int driverId,String startTime, String endTime,
                Location startLocation, Location endLocation, TripStatus status,
                int score, int distance, List<Event> events){
        this.id = id;
        this.driverId = driverId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.status = status;
        this.score = score;
        this.distance = distance;
        this.drivingEvents = events;

    }
}
