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
import com.example.driveguard.objects.CompletedTrip;
import com.example.driveguard.objects.Trip;

import trip_data.Event;

public class CompletedTripDialog extends DialogFragment {

    private CompletedTrip completedTrip;
    private Trip trip;

    public void setCompletedTrip(CompletedTrip completedTrip) {

        this.completedTrip = completedTrip;

    }

    public void setTrip(Trip trip) {

        this.trip = trip;

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View dialogView = inflater.inflate(R.layout.completed_trip_dialog, container, false);

        TextView scoreTextView = dialogView.findViewById(R.id.textViewHistoryDriverScore);
        TextView distanceTextView = dialogView.findViewById(R.id.distance);
        TextView durationTextView = dialogView.findViewById(R.id.duration);
        TextView tripLengthTextView = dialogView.findViewById(R.id.tripLength);

        LinearLayout deductionsHistoryLayout = dialogView.findViewById(R.id.linearLayoutHistoryDeductions);

        if(completedTrip != null) {

            scoreTextView.setText(completedTrip.getScore());
            distanceTextView.setText(completedTrip.getDistanceKM() + " km");
            durationTextView.setText(completedTrip.getDuration());
            tripLengthTextView.setText(completedTrip.getTripLength().getFormattedTime());

        }

        if(trip.getDrivingEvents() != null && !trip.getDrivingEvents().isEmpty()) {

            for(Event event : trip.getDrivingEvents()) {

                TextView deductionHistoryTextView = new TextView(getActivity());
                deductionHistoryTextView.setText("- " + event);
                deductionsHistoryLayout.addView(deductionHistoryTextView);

            }

        } else {

            TextView perfectDriving = new TextView(getActivity());
            perfectDriving.setText("No deductions for this trip.");
            deductionsHistoryLayout.addView(perfectDriving);

        }

        return dialogView;
    }

}
