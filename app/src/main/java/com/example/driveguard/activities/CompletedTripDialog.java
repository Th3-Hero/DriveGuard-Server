package com.example.driveguard.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.driveguard.R;
import com.example.driveguard.objects.DrivingEvent;
import com.example.driveguard.objects.Trip;

import com.example.driveguard.objects.Event;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CompletedTripDialog extends DialogFragment {
    private Trip trip;

    public void setTrip(Trip trip) {

        this.trip = trip;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View dialogView = inflater.inflate(R.layout.completed_trip_dialog, container, false);

        TextView scoreTextView = dialogView.findViewById(R.id.textViewHistoryDriverScore);
        TextView startTimeTextView = dialogView.findViewById(R.id.startHistoryTime);
        TextView endTimeTextView = dialogView.findViewById(R.id.endHistoryTime);
        TextView startLocationTextView = dialogView.findViewById(R.id.startHistoryLocation);
        TextView endLocationTextView = dialogView.findViewById(R.id.endHistoryLocation);
        TextView distance = dialogView.findViewById(R.id.distanceHistory);
        LinearLayout deductionsHistoryLayout = dialogView.findViewById(R.id.linearLayoutHistoryDeductions);

        if(trip != null) {

            scoreTextView.setText(String.valueOf(trip.getScore()));

            if(trip.getStartTime() != null) {
                startTimeTextView.setText(formatDate(trip.getStartTime()));
            }

            if (trip.getEndTime() != null) {
                endTimeTextView.setText(formatDate(trip.getEndTime()));
            }

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

            distance.setText(String.valueOf(trip.getDistance()) + " km");

            // adding deductions dynamically (some people drive perfectly)

            if(trip.getDrivingEvents() != null && !trip.getDrivingEvents().isEmpty()) {

                for(DrivingEvent event : trip.getDrivingEvents()) {

                    TextView deductionTextView = new TextView(getActivity());
                    deductionTextView.setText("- " + event);
                    deductionsHistoryLayout.addView(deductionTextView);

                }

            } else {

                TextView perfectDriving = new TextView(getActivity());
                perfectDriving.setText("No deductions for this trip.");
                deductionsHistoryLayout.addView(perfectDriving);

            }

        }
        return dialogView;

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String formatDate(String time){
        String formattedDate = time;

        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS]");

            // Parse the string into a LocalDateTime
            LocalDateTime parsedDate = LocalDateTime.parse(formattedDate, formatter);

            // Format it back into a standard string (or any other desired format)
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            formattedDate = parsedDate.format(outputFormatter);


        } catch (DateTimeException e) {

            formattedDate = "Invalid date format";

        }
        return formattedDate;
    }
}
