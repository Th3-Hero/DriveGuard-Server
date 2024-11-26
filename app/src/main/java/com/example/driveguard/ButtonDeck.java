package com.example.driveguard;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.example.driveguard.activities.HomeScreen;
import com.example.driveguard.activities.TripScreen;
import com.example.driveguard.objects.Credentials;

public class ButtonDeck {
public static void SetUpButtons(Activity activity, Credentials credentials){

        final Button _homeButton = activity.findViewById(R.id.homeButton);
        _homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, HomeScreen.class);
                intent.putExtra("driverID", credentials.getDriverId());
                intent.putExtra("token", credentials.getToken());
                activity.startActivity(intent);
            }
        });

        final Button tripButton = activity.findViewById(R.id.trip);
        tripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TripScreen.class);
                intent.putExtra("driverID", credentials.getDriverId());
                intent.putExtra("token", credentials.getToken());
                activity.startActivity(intent);
            }
        });

    }
    public static void ToggleButtons(Activity activity){
        Button home = activity.findViewById(R.id.homeButton);
        Button previousTrips = activity.findViewById(R.id.previousTrips);
        Button trip = activity.findViewById(R.id.trip);
        Button help = activity.findViewById(R.id.help);
        home.setEnabled(!home.isEnabled());
        previousTrips.setEnabled(!previousTrips.isEnabled());
        trip.setEnabled(!trip.isEnabled());
        help.setEnabled(!help.isEnabled());
    }
}
