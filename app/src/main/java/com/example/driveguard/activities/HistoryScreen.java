package com.example.driveguard.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.driveguard.ButtonDeck;
import com.example.driveguard.GsonUtilities;
import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.objects.CompletedTrip;
import com.example.driveguard.objects.TripHistoryAdapter;

import java.io.IOException;
import java.util.List;

import lombok.SneakyThrows;
import okhttp3.Response;


public class HistoryScreen extends AppCompatActivity {

    private RecyclerView recyclerViewTripHistory;
    private NetworkManager networkManager;
    private TripHistoryAdapter tripHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_suggestion_screen);

        networkManager = new NetworkManager(getApplicationContext());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        recyclerViewTripHistory = findViewById(R.id.recyclerViewTripHistory);
        recyclerViewTripHistory.setLayoutManager(new LinearLayoutManager(this));

        ButtonDeck.SetUpButtons(this);

        getTripHistory(this);

    }

    private void getTripHistory(Context context) {

        Response response;

        try{

            response = networkManager.getListOfTrips();

            if(response.isSuccessful()) {

                //assert response.body() != null;
                List<CompletedTrip> completedTrips = parseTripResponse(response.body().string());

                tripHistoryAdapter = new TripHistoryAdapter(completedTrips);
                recyclerViewTripHistory.setAdapter(tripHistoryAdapter);

            } else {

                Toast.makeText(this, "Failed to load trip history", Toast.LENGTH_SHORT).show();

            }

        } catch (IOException e) {

            Toast.makeText(this,"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }
    @Nullable
    private List<CompletedTrip> parseTripResponse(String response) {

        if(response == null|| response.isEmpty()) {

            Toast.makeText(this, "Empty response from server", Toast.LENGTH_SHORT).show();
            return null;

        }

        return GsonUtilities.JsonToCompletedTripList(response);

    }

}
