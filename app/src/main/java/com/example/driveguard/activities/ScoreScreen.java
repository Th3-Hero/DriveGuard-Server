package com.example.driveguard.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.driveguard.R;
import com.example.driveguard.objects.DrivingEvent;
import com.example.driveguard.objects.Event;
import com.example.driveguard.objects.Trip;

import java.util.Objects;

// once trip is finished, call the scoring from trip.java
// call the score as a dialogue box, once clicked it disappears
// that trip will be passed to the trip history which then displays it as an item (includes start time, end time, start location and end location)

public class ScoreScreen extends DialogFragment {
    private Trip trip;

    public void setTrip(Trip trip) {

       this.trip = trip;

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View dialogView = inflater.inflate(R.layout.average_score_screen, (ViewGroup) container, false);

        LinearLayout deductionsLayout = dialogView.findViewById(R.id.linearLayoutDeductions);

        TextView scoreTextView = dialogView.findViewById(R.id.textViewDriverScore);
        TextView startTimeTextView = dialogView.findViewById(R.id.startTime);
        TextView endTimeTextView = dialogView.findViewById(R.id.endTime);
        TextView startLocationTextView = dialogView.findViewById(R.id.startLocation);
        TextView endLocationTextView = dialogView.findViewById(R.id.endLocation);
        TextView distanceTextView = dialogView.findViewById(R.id.distance);

        if(trip != null) {

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

                for(DrivingEvent event : trip.getDrivingEvents()) {

                    TextView deductionTextView = new TextView(getActivity());
                    deductionTextView.setText("- " + event);
                    deductionsLayout.addView(deductionTextView);

                }

            } else {

                TextView perfectDriving = new TextView(getActivity());
                perfectDriving.setText("No deductions for this trip.");
                deductionsLayout.addView(perfectDriving);

            }

        }
        return dialogView;
    }

   @Override
    public void onStart() {

       super.onStart();
       if(getDialog() != null) {

           Objects.requireNonNull(getDialog().getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

       }

   }

}
