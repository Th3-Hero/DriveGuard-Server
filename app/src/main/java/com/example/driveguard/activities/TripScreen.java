package com.example.driveguard.activities;

import static com.example.driveguard.GsonUtilities.JsonToTrip;
import static com.example.driveguard.Utilities.getCredentialsFromExtras;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.driveguard.ButtonDeck;
import com.example.driveguard.DataClassifier;
import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.Utilities;
import com.example.driveguard.objects.Credentials;
import com.example.driveguard.objects.Road;
import com.example.driveguard.objects.Trip;

import java.io.IOException;

import lombok.SneakyThrows;
import okhttp3.Response;
import com.example.driveguard.DataCollector;
import com.google.gson.Gson;

public class TripScreen extends AppCompatActivity {

    private DataCollector dataCollector;

    private DataClassifier dataClassifier;
    private Trip currentTrip;
    private final int START_TRIP_SUCCESS = 201;
    private final int STOP_TRIP_SUCCESS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_screen);

        //allows the UI thread to perform network calls. We could make them async if this causes issues
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dataCollector = new DataCollector(getApplicationContext());

        //defines the toolbar used in the activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toggle button that is used to stop and start trips
        ToggleButton startButton = findViewById(R.id.startButton);

        ButtonDeck.SetUpButtons(this);

        // Create a handler and runnable for the async loop
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Check for events periodically
                checkForEvents();

                // Schedule the next execution after 25 milliseconds
                handler.postDelayed(this, 25);
            }
        };

        startButton.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View v) {
                ButtonDeck.ToggleButtons(TripScreen.this);

                NetworkManager networkManager = new NetworkManager(getApplicationContext());

                //start trip here
                if (startButton.isChecked()){//for starting trip
                    // 201 means a trip was started successfully
                    Response response = networkManager.StartTrip(dataCollector.getStartingLocation());
                    if (response != null && response.code() == START_TRIP_SUCCESS){
                        //networkManager.dataCollector.startDataCollection();
                        Toast.makeText(TripScreen.this, "Trip Successfully started!", Toast.LENGTH_LONG).show();

                        try {
                            assert response.body() != null;
                            currentTrip = JsonToTrip(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //retrieve the trip ID sent by server
                            //credentials.setTripId(currentTrip.getId());

                        Utilities.SaveTripID(getApplicationContext(), currentTrip.getId());

                        //BROOKE you can add all your trip stuff here
                        // Start the async loop
                        handler.postDelayed(runnable, 25);

//                        run an async loop to check for events every 25 millisecond
//                        look at timeline from javafx

                    } else {
                        assert response != null;
                        if (response.code() == 409) {//code 409 means a trip is already underway if this is the case call to end it
                            Response tripResponse = networkManager.getCurrentTrip();

                            if (tripResponse.isSuccessful()){
                                assert tripResponse.body() != null;
                                Trip current = JsonToTrip(tripResponse.body().string());
                                Utilities.SaveTripID(getApplicationContext(), current.getId());
                            }

                            Response endResponse = networkManager.EndTrip(dataCollector.getStartingLocation());

                            if (endResponse.isSuccessful()){
                                Toast.makeText(TripScreen.this, "ERROR: cancelling previous trip", Toast.LENGTH_LONG).show();
                            }
                            startButton.setChecked(false);
                        }
                        else{//set button back to unchecked
                            Toast.makeText(TripScreen.this, "ERROR: " + response.code(), Toast.LENGTH_LONG).show();
                            startButton.setChecked(false);
                        }
                    }
                }

                else if(!startButton.isChecked()){
                    Response response = networkManager.EndTrip(dataCollector.getStartingLocation());
                    if (response != null && response.code() == STOP_TRIP_SUCCESS) {
                        //networkManager.dataCollector.stopDataCollection();

                        Toast.makeText(TripScreen.this, "Trip successfully ended!", Toast.LENGTH_LONG).show();

                        // Stop the async loop
                        handler.removeCallbacks(runnable);

                        // Score screen ic called - Stephan

                        ScoreScreen scoreScreen = new ScoreScreen();

                        scoreScreen.setTrip(currentTrip);

                        scoreScreen.show(getSupportFragmentManager(), "ScoreScreenDialog");
                    }
                    }
                }
            });
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        if (id == R.id.settings){
            Intent intent = new Intent(TripScreen.this, Settings.class);
            startActivity(intent);
        }
        else if (id == R.id.profile){
            Intent intent;
            intent = new Intent(this, ProfileScreen.class);
            startActivity(intent);
        }
        return true;
    }

    // Method to check for driving events
    private void checkForEvents() {
        // Get current data from the DataCollector
        float speed = dataCollector.getSpeed();              // Current speed
        float gForce = dataCollector.getAcceleration();      // Current g-force
        float turningRate = dataCollector.getTurningRate();  // Current turning rate
        android.location.Location location = dataCollector.getStartingLocation(); // Current location
        String timestamp = String.valueOf(System.currentTimeMillis()); // Current timestamp

        // Use NetworkManager for additional data retrieval
        NetworkManager networkManager = new NetworkManager(getApplicationContext());
        int postedSpeedLimit = 0; // getting the posted speed limit from the current road
        try {
            // Make the API call to fetch road data
            Response response = networkManager.getRoadFromLocation(location);

            // Check if the response is successful
            if (response.isSuccessful() && response.body() != null) {
                // Parse the response body into a Road object
                String responseBody = response.body().string();
                Gson gson = new Gson();
                Road road = gson.fromJson(responseBody, Road.class);

                // Return the speed limit
                postedSpeedLimit = road.getSpeedLimit();
            } else {
                // Log error or handle unsuccessful response
                System.err.println("Failed to fetch road data. HTTP Code: " + response.code());
            }
        } catch (IOException e) {
            // Handle exceptions
            System.err.println("Error fetching road data: " + e.getMessage());
        }
        dataClassifier = new DataClassifier(postedSpeedLimit);

        // Classify the data for events
        dataClassifier.classifyData(speed, gForce, turningRate, timestamp, networkManager, location);
    }
}




