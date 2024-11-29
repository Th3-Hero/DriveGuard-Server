package com.example.driveguard;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.example.driveguard.activities.HistoryScreen;
import com.example.driveguard.activities.HomeScreen;
import com.example.driveguard.activities.TripScreen;

public class ButtonDeck {
public static void SetUpButtons(@NonNull Activity activity){

        ImageButton homeButton = activity.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.getClass() != HomeScreen.class) {
                    Intent intent = new Intent(activity, HomeScreen.class);
                    activity.startActivity(intent);
                }
            }
        });

        final ImageButton tripButton = activity.findViewById(R.id.trip);
        tripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.getClass() != TripScreen.class) {
                    Intent intent = new Intent(activity, TripScreen.class);
                    activity.startActivity(intent);
                }
            }
        });

        final ImageButton historyButton = activity.findViewById(R.id.previousTrips);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.getClass() != HistoryScreen.class) {
                    Intent intent = new Intent(activity, HistoryScreen.class);
                    activity.startActivity(intent);
                }
            }
        });
}
    public static void ToggleButtons(@NonNull Activity activity){
        ImageButton home = activity.findViewById(R.id.homeButton);
        ImageButton previousTrips = activity.findViewById(R.id.previousTrips);
        ImageButton trip = activity.findViewById(R.id.trip);
        ImageButton help = activity.findViewById(R.id.help);
        home.setEnabled(!home.isEnabled());
        previousTrips.setEnabled(!previousTrips.isEnabled());
        trip.setEnabled(!trip.isEnabled());
        help.setEnabled(!help.isEnabled());
    }
}
