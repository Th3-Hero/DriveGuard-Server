package com.example.driveguard.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.driveguard.R;
import com.example.driveguard.objects.Trip;

public class ScoreScreen extends AppCompatActivity {

    // once trip is finished, call the scoring from trip.java
    // call the score as a dialogue box, once clicked it disappears
    // that trip will be passed to the trip history which then displays it as an item (includes start time, end time, start location and end location)

    private Trip trip;

   @Override
    protected void onCreate(Bundle savedInstanceState) {

       super.onCreate(savedInstanceState);

       if(trip != null) {

           showTripSumDialog(trip);

       } else {

           finish();

       }

   }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    private void showTripSumDialog(Trip trip) {

       View dialogView = LayoutInflater.from(this).inflate(R.layout.score_screen, null);

       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setView(dialogView);

       @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout deductionsLayout = dialogView.findViewById(R.id.linearLayoutDeductions);

       @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView scoreTextView = dialogView.findViewById(R.id.textViewDriverScore);
       @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView startTimeTextView = dialogView.findViewById(R.id.startTime);
       @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView endTimeTextView = dialogView.findViewById(R.id.endTime);
       @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView startLocationTextView = dialogView.findViewById(R.id.startLocation);
       @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView endLocationTextView = dialogView.findViewById(R.id.endLocation);
       @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView distanceTextView = dialogView.findViewById(R.id.distance);

       scoreTextView.setText(String.valueOf(trip.getScore()));

        startTimeTextView.setText(trip.getStartTime());
        endTimeTextView.setText(trip.getEndTime());

        if(trip.getStartLocation() != null) {

            startLocationTextView.setText("Lat: " + trip.getStartLocation().getLatitude() + ", Lng: " + trip.getStartLocation().getLongitude());

        } else {

            startLocationTextView.setText("N/A");
            Toast.makeText(ScoreScreen.this,"Start location not available.", Toast.LENGTH_SHORT).show();

        }

        if(trip.getEndLocation() != null) {

            endLocationTextView.setText("Lat: " + trip.getEndLocation().getLatitude() + ", Lng: " + trip.getEndLocation().getLongitude());
            Toast.makeText(ScoreScreen.this, "End location not available.", Toast.LENGTH_SHORT).show();

        } else {

            endLocationTextView.setText("N/A");
            Toast.makeText(ScoreScreen.this,"End location not available.", Toast.LENGTH_SHORT).show();

        }

        distanceTextView.setText(trip.getDistance() + " km");

        // adding deductions dynamically (some people drive perfectly)

       if(trip.getDrivingEvents() != null && !trip.getDrivingEvents().isEmpty()) {

           for(int i = 0; i < trip.getDrivingEvents().size(); i++) {

               TextView deductionTextView = new TextView(this);
               deductionTextView.setText("- " + trip.getDrivingEvents().get(i));
               deductionsLayout.addView(deductionTextView);

           }

       } else {

           TextView perfectDriving = new TextView(this);
           perfectDriving.setText("No deductions for this trip.");
           deductionsLayout.addView(perfectDriving);

       }

       AlertDialog dialog = builder.create();
       dialog.show();

   }

}
