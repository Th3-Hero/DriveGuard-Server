package com.example.driveguard.objects;

import android.location.Location;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import trip_data.Event;

@Getter
@Setter
public class Trip {

    private int id;
    private int driverId;
    private String startTime;
    private String endTime;
    private Location startLocation;
    private Location endLocation;
    private String status;//possibly make enum
    private int score;
    private int distance;
    private Event[] tripEvents;

}
