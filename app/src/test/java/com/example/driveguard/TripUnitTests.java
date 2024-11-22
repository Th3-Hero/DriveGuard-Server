package com.example.driveguard;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


import com.example.driveguard.objects.Trip;
import com.example.driveguard.objects.TripStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TripUnitTests {

    @Test
    public void TripToJson(){
        Trip trip = new Trip(1, 1, "1:30", "2:30",
        null, null, TripStatus.IN_PROGRESS, 1, 1, null);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String output = gson.toJson(trip);
        System.out.println(output);
        assertNotEquals(output, null);
    }
    @Test
    public void JsonToTrip(){
        Trip trip = new Trip(1, 1, "1:30", "2:30",
                null, null, TripStatus.IN_PROGRESS, 1, 1, null);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String output = gson.toJson(trip);

        Trip trip1 = gson.fromJson(output, Trip.class);

        assertNotEquals(trip1, null);
    }
}
