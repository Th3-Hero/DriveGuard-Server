package com.example.driveguard.activities;

import static com.example.driveguard.GsonUtilities.JsonToTrip;

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
import com.example.driveguard.objects.Credentials;
import com.example.driveguard.objects.Road;
import com.example.driveguard.objects.Trip;

import java.io.IOException;

import okhttp3.Response;
import com.example.driveguard.DataCollector;
import com.google.gson.Gson;

public class TripScreen extends AppCompatActivity {

    private DataCollector dataCollector;

    private DataClassifier dataClassifier;
    private Credentials credentials;
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

        //Used to retrieve the driverID and login token from the previous activity
        Bundle extras = getIntent().getExtras();
        credentials = getCredentials(extras);

        //defines the toolbar used in the activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toggle button that is used to stop and start trips
        ToggleButton startButton = findViewById(R.id.startButton);

        ButtonDeck.SetUpButtons(this, credentials);

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
            @Override
            public void onClick(View v) {
                ButtonDeck.ToggleButtons(TripScreen.this);

                NetworkManager networkManager = new NetworkManager();

                //start trip here
                if (startButton.isChecked()){//for starting trip
                    // 201 means a trip was started successfully
                    Response response = networkManager.StartTrip(credentials, dataCollector.getStartingLocation());
                    if (response != null && response.code() == START_TRIP_SUCCESS) {
                        Toast.makeText(TripScreen.this, "Trip Successfully started!", Toast.LENGTH_LONG).show();

                        try {
                            assert response.body() != null;
                            currentTrip = JsonToTrip(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        // Retrieve the trip ID sent by the server
                        credentials.setTripId(currentTrip.getId());

                        // Start the async loop
                        handler.postDelayed(runnable, 25);

                    } else { // Set button back to unchecked
                        startButton.setChecked(false);
                    }
                } else if (!startButton.isChecked()) { // For ending trip
                    Response response = networkManager.EndTrip(credentials, dataCollector.getStartingLocation());
                    if (response != null && response.code() == STOP_TRIP_SUCCESS) {
                        Toast.makeText(TripScreen.this, "Trip successfully ended!", Toast.LENGTH_LONG).show();

                        // Stop the async loop
                        handler.removeCallbacks(runnable);
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
            intent.putExtra("driverId", credentials.getDriverId());
            intent.putExtra("token", credentials.getToken());
            startActivity(intent);
        }
        return true;
    }

    public static Credentials getCredentials(Bundle extras){
        if (extras != null){
            int driverID = extras.getInt("driverID");
            String token = extras.getString("token");
            return new Credentials(driverID, token);
        } else {
            return new Credentials();
        }
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
        NetworkManager networkManager = new NetworkManager();
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
        dataClassifier.classifyData(speed, gForce, turningRate, timestamp, networkManager, credentials, location);
    }

}


