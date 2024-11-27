package com.example.driveguard.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


import com.example.driveguard.R;
import com.example.driveguard.objects.Trip;

import java.util.Objects;

import trip_data.Event;

// once trip is finished, call the scoring from trip.java
// call the score as a dialogue box, once clicked it disappears
// that trip will be passed to the trip history which then displays it as an item (includes start time, end time, start location and end location)

public class ScoreScreen extends DialogFragment {
    private Trip trip;

   public void setTrip(Trip trip) {

       this.trip = trip;

   }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    public View onCreateView(LayoutInflater inflater, View container, Bundle savedInstanceState) {

       View dialogView = inflater.inflate(R.layout.score_screen, (ViewGroup) container, false);

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

        }

        if(trip.getEndLocation() != null) {

            endLocationTextView.setText("Lat: " + trip.getEndLocation().getLatitude() + ", Lng: " + trip.getEndLocation().getLongitude());

        } else {

            endLocationTextView.setText("N/A");

        }

        distanceTextView.setText(trip.getDistance() + " km");

        // adding deductions dynamically (some people drive perfectly)

       if(trip.getDrivingEvents() != null && !trip.getDrivingEvents().isEmpty()) {

           for(Event event : trip.getDrivingEvents()) {

               TextView deductionTextView = new TextView(getActivity());
               deductionTextView.setText("- " + event);
               deductionsLayout.addView(deductionTextView);

           }

       } else {

           TextView perfectDriving = new TextView(getActivity());
           perfectDriving.setText("No deductions for this trip.");
           deductionsLayout.addView(perfectDriving);

       }

       return dialogView;

   }

   @Override
    public void onStart() {

       super.onStart();

       AlertDialog dialog = (AlertDialog) getDialog();
       if(dialog != null) {

           Objects.requireNonNull(dialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

       }

   }

}
