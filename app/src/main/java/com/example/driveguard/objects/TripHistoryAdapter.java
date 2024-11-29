package com.example.driveguard.objects;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.driveguard.R;

import java.util.List;

public class TripHistoryAdapter extends RecyclerView.Adapter<TripHistoryAdapter.TripHistoryViewHolder> {

    private final List<CompletedTrip> completedTrips;

    public TripHistoryAdapter(List<CompletedTrip> completedTrips) {

        this.completedTrips = completedTrips;

    }

    @NonNull
    @Override
    public TripHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_trip_dialog, parent, false);
        return new TripHistoryViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TripHistoryViewHolder holder, int position) {

        CompletedTrip trip = completedTrips.get(position);

        holder.scoreTextView.setText(String.valueOf(trip.getScore()));
        holder.distanceTextView.setText(String.valueOf(trip.getDistanceKM()) + " km");
        holder.tripLengthTextView.setText(trip.getDuration().getFormattedTime());

    }

    @Override
    public int getItemCount(){

        return completedTrips.size();

    }

    public static class TripHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView scoreTextView, distanceTextView, tripLengthTextView;
        LinearLayout linearLayoutHistoryAttributes;
        public TripHistoryViewHolder(View itemView) {

            super(itemView);
            scoreTextView = itemView.findViewById(R.id.textViewHistoryDriverScore);
            distanceTextView = itemView.findViewById(R.id.distance);
            tripLengthTextView = itemView.findViewById(R.id.tripLength);

            linearLayoutHistoryAttributes = itemView.findViewById(R.id.linearLayoutHistoryAttributes);

        }

    }

}
