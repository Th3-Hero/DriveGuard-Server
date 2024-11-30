package com.example.driveguard.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.driveguard.ButtonDeck;
import com.example.driveguard.GsonUtilities;
import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.objects.CompletedTrip;
import com.example.driveguard.objects.Driver;
import com.example.driveguard.objects.TripHistoryAdapter;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public class HistoryScreen extends AppCompatActivity {

    private RecyclerView recyclerViewTripHistory;
    private NetworkManager networkManager;
    private TripHistoryAdapter tripHistoryAdapter;
    private TextView avgDriverScore;
    private Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_suggestion_screen);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        networkManager = new NetworkManager(getApplicationContext());

        recyclerViewTripHistory = findViewById(R.id.recyclerViewTripHistory);
        recyclerViewTripHistory.setLayoutManager(new LinearLayoutManager(this));

        ButtonDeck.SetUpButtons(this);
        ButtonDeck.TintButton(this);
        getTripHistory(this);

        try {
            getAverageScore(networkManager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void getAverageScore (@NonNull NetworkManager networkManager) throws IOException {

        Response response = networkManager.getDriver();
        avgDriverScore = findViewById(R.id.textViewAverageScore);

        if (response != null && response.isSuccessful()) {

            assert response.body() != null;
            Driver driver = GsonUtilities.JsonToDriver(response.body().string());
            if (driver != null) {

                avgDriverScore.setText(String.valueOf(driver.getOverallScore()));

            }
        } else {

            Toast.makeText(this, "Unable to retrieve average score", Toast.LENGTH_SHORT).show();
            avgDriverScore.setText("0");

        }

    }

    private void getTripHistory(Context context) {

        Response response;

        try{

            response = networkManager.getListOfTrips();

            if(response.isSuccessful()) {

                assert response.body() != null;
                List<CompletedTrip> completedTrips = parseTripResponse(response.body().string());

                tripHistoryAdapter = new TripHistoryAdapter(completedTrips, this);
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
