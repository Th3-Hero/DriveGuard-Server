package com.example.driveguard.objects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;

import java.util.List;

public class DrivingEventsAdapter extends RecyclerView.Adapter<DrivingEventsAdapter.EventViewHolder>{

    private final List<DrivingEvent> drivingEvents;
    private NetworkManager networkManager;
    private final Context context;


    public DrivingEventsAdapter(List<DrivingEvent> drivingEvents, Context context) {

        this.drivingEvents = drivingEvents;
        this.context = context;

    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.driving_event_item, parent, false);
        return new EventViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        DrivingEvent event = drivingEvents.get(position);

        networkManager = new NetworkManager(context.getApplicationContext());
        holder.eventTypeTextView.setText(event.getClass().getSimpleName());
        //holder.eventTimeTextView.setText("Time: " + event.getPointsDeducted());
        holder.eventTypeTextView.setText("Points deducted: " + event.getPointsDeducted());

    }

    @Override
    public int getItemCount() {
        return drivingEvents != null ? drivingEvents.size() : 0;
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView eventTypeTextView;
        TextView eventTimeTextView;

        public EventViewHolder (@NonNull View itemView) {

            super(itemView);
            eventTypeTextView = itemView.findViewById(R.id.eventTypeTextView);
            eventTimeTextView = itemView.findViewById(R.id.eventTimeTextView);

        }

    }

}
