package com.example.driveguard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ButtonDeck {
public static void SetUpButtons(Activity activity){

        final Button _homeButton = activity.findViewById(R.id.homeButton);
        _homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, HomeScreen.class);
                activity.startActivity(intent);
            }
        });

        final Button tripButton = activity.findViewById(R.id.trip);
        tripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TripScreen.class);

                activity.startActivity(intent);
            }
        });

    }
}
