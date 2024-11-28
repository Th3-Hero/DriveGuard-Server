package com.example.driveguard.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.driveguard.GsonUtilities;
import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.objects.CompletedTrip;
import com.example.driveguard.objects.Credentials;
import com.example.driveguard.objects.TripHistoryAdapter;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public class HistoryScreen extends AppCompatActivity {

    private RecyclerView recyclerViewTripHistory;
    private NetworkManager networkManager;
    private TripHistoryAdapter tripHistoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_suggestion_screen);

        recyclerViewTripHistory = findViewById(R.id.recyclerViewTripHistory);
        recyclerViewTripHistory.setLayoutManager(new LinearLayoutManager(this));

        networkManager = new NetworkManager(getApplicationContext());

        Credentials credentials = getCredentialsforHistory();
        getTripHistory(credentials);

    }

    private void getTripHistory(Credentials credentials) {

        try{

            Response response = networkManager.getListOfTrips();

            if(response.isSuccessful()) {

                assert response.body() != null;
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

    private List<CompletedTrip> parseTripResponse(String response) {

        return GsonUtilities.JsonToCompletedTripList(response);

    }

    private Credentials getCredentialsforHistory() {

        return new Credentials(getCredentialsforHistory().getDriverId(), getCredentialsforHistory().getToken());

    }

}
