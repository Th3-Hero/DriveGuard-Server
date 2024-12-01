package com.example.driveguard.activities;

import static com.example.driveguard.GsonUtilities.JsonToTrip;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.driveguard.ButtonDeck;
import com.example.driveguard.DataClassifier;
import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.Utilities;
import com.example.driveguard.objects.Credentials;
import com.example.driveguard.objects.DrivingEvent;
import com.example.driveguard.objects.DrivingEventsAdapter;
import com.example.driveguard.objects.Road;
import com.example.driveguard.objects.Trip;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.SneakyThrows;
import okhttp3.Response;
import com.example.driveguard.DataCollector;
import com.example.driveguard.objects.Weather;
import com.google.gson.Gson;

public class TripScreen extends AppCompatActivity {

    private DataCollector dataCollector;
    private DrivingEventsAdapter drivingEventsAdapter;
    private RecyclerView recyclerViewTripHistory;
    private NetworkManager networkManager;
    private DataClassifier dataClassifier;
    private Credentials credentials;
    private Trip currentTrip;
    private final int START_TRIP_SUCCESS = 201;
    private final int STOP_TRIP_SUCCESS = 200;
    private TextView limitText;
    private TextView speedText;
    private float speed;
    private float limit;

    private Map<String, Boolean> eventHasBeenDetected = new HashMap<>();

    public Map<String, Boolean> getEventHasBeenDetected() {
        return eventHasBeenDetected;
    }

    public void setEventHasBeenDetected(Map<String, Boolean> eventHasBeenDetected) {
        this.eventHasBeenDetected = eventHasBeenDetected;
    }

    private Date timeLastChecked30Min = new Date();
    // Getter
    public Date getTimeLastChecked30Min() {
        return timeLastChecked30Min;
    }

    // Setter
    public void setTimeLastChecked30Min(Date timeLastChecked30Min) {
        this.timeLastChecked30Min = timeLastChecked30Min;
    }
    private Date timeLastChecked15Sec = new Date();


    public Date getTimeLastChecked15Sec() {
        return timeLastChecked15Sec;
    }

    public void setTimeLastChecked15Sec(Date timeLastChecked15Sec) {
        this.timeLastChecked15Sec = timeLastChecked15Sec;
    }

    private Weather currentWeather;

    private int postedSpeedLimit;

    // Getter for currentWeather
    public Weather getCurrentWeather() { return currentWeather;}
    // Setter for currentWeather
    public void setCurrentWeather(Weather currentWeather) {
        this.currentWeather = currentWeather;
    }

    // Getter for postedSpeedLimit
    public int getPostedSpeedLimit() {
        return postedSpeedLimit;
    }

    // Setter for postedSpeedLimit
    public void setPostedSpeedLimit(int postedSpeedLimit) {
        this.postedSpeedLimit = postedSpeedLimit;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_screen);

        limitText = findViewById(R.id.limit);
        speedText = findViewById(R.id.speed);

        //allows the UI thread to perform network calls. We could make them async if this causes issues
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dataCollector = new DataCollector(getApplicationContext());

        NetworkManager networkManager = new NetworkManager(getApplicationContext());

        RequestWeather(dataCollector.getStartingLocation());
        RequestRoad(dataCollector.getStartingLocation());

        //defines the toolbar used in the activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toggle button that is used to stop and start trips
        ToggleButton startButton = findViewById(R.id.startButton);

        ButtonDeck.SetUpButtons(this);
        ButtonDeck.TintButton(this);

        // Create a handler and runnable for the async loop
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Check for events periodically
                checkForEvents();

                DrivingEvent lastEvent = networkManager.getLastEvent();
                if (lastEvent != null) {
                    String event = lastEvent.toString();
                    TextView e = findViewById(R.id.event);
                    e.setText(event);
                }
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
                if (startButton.isChecked()) {//for starting trip
                    // 201 means a trip was started successfully
                    Response response = networkManager.StartTrip(dataCollector.getStartingLocation());
                    if (response != null && response.code() == START_TRIP_SUCCESS) {

                        Toast.makeText(TripScreen.this, "Trip Successfully started!", Toast.LENGTH_LONG).show();

                        ButtonDeck.ToggleButtons(TripScreen.this);
                        dataCollector.startDataCollection();

                        try {
                            assert response.body() != null;
                            currentTrip = JsonToTrip(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        Utilities.SaveTripID(getApplicationContext(), currentTrip.getId());

                        // Start the async loop
                        handler.postDelayed(runnable, 25);

                    } else {
                        assert response != null;
                        if (response.code() == 409) {//code 409 means a trip is already underway if this is the case call to end it
                            Response tripResponse = networkManager.getCurrentTrip();

                            if (tripResponse.isSuccessful()) {
                                assert tripResponse.body() != null;
                                Trip current = JsonToTrip(tripResponse.body().string());
                                Utilities.SaveTripID(getApplicationContext(), current.getId());
                            }

                            Response endResponse = networkManager.EndTrip(dataCollector.getStartingLocation());

                            if (endResponse.isSuccessful()) {
                                Toast.makeText(TripScreen.this, "ERROR: cancelling previous trip", Toast.LENGTH_LONG).show();
                            }
                            startButton.setChecked(false);
                        } else { // Set button back to unchecked
                            Toast.makeText(TripScreen.this, "ERROR: " + response.code(), Toast.LENGTH_LONG).show();
                            startButton.setChecked(false);
                        }
                    }
                } else if (!startButton.isChecked()) { // For ending trip
                    Response response = networkManager.EndTrip(dataCollector.getStartingLocation());
                    if (response != null && response.code() == STOP_TRIP_SUCCESS) {

                        Toast.makeText(TripScreen.this, "Trip successfully ended!", Toast.LENGTH_LONG).show();

                        // Stop the async loop
                        handler.removeCallbacks(runnable);

                        dataCollector.stopDataCollection();

                        ButtonDeck.ToggleButtons(TripScreen.this);

                        // SCORE SCREEN IS CALLED

                        ScoreScreen scoreScreen = new ScoreScreen();
                        scoreScreen.setTrip(currentTrip);
                        scoreScreen.show(getSupportFragmentManager(), "ScoreScreen");

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
        //float speed = dataCollector.getSpeed();              // Current speed
        float speed = 65f;
        float gForce = dataCollector.getAcceleration();      // Current g-force
        float turningRate = dataCollector.getTurningRate();  // Current turning rate
        Location location = dataCollector.getStartingLocation(); // Current location

        String timestamp = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant currentTime = Instant.now();
            timestamp = DateTimeFormatter.ISO_INSTANT.format(currentTime);
        }
       // String timestamp = String.valueOf(System.currentTimeMillis()); // Current timestamp

        String s = "Your Speed: " + String.valueOf(Math.round(speed));
        speedText.setText(s);

        String l = "Speed Limit: " + String.valueOf(getPostedSpeedLimit());
        limitText.setText(l);

        if(this.getTimeLastChecked15Sec().getTime() >= this.getTimeLastChecked15Sec().getTime() + 15 * 1000)
        {
            this.getEventHasBeenDetected().put("speed", false);
            this.getEventHasBeenDetected().put("accelerate", false);
            this.getEventHasBeenDetected().put("brake", false);
            this.getEventHasBeenDetected().put("turn", false);
        }

        // Use NetworkManager for additional data retrieval
        //NetworkManager networkManager = new NetworkManager(getApplicationContext());
        if(this.getTimeLastChecked30Min().getTime() >= this.getTimeLastChecked30Min().getTime() + 30 * 60 * 1000)
        {
                RequestWeather(location);
                RequestRoad(location);
//            try {
//                // Make the API call to fetch road data
//                Response response = networkManager.getRoadFromLocation(location);
//
//                // Check if the response is successful
//                if (response.isSuccessful() && response.body() != null) {
//                    // Parse the response body into a Road object
//                    String responseBody = response.body().string();
//                    Gson gson = new Gson();
//                    Road road = gson.fromJson(responseBody, Road.class);
//
//                    // Return the speed limit
//                    this.setPostedSpeedLimit(road.getSpeedLimit());
//                } else {
//                    // Log error or handle unsuccessful response
//                    System.err.println("Failed to fetch road data. HTTP Code: " + response.code());
//                }
//            } catch (IOException e) {
//                // Handle exceptions
//                System.err.println("Error fetching road data: " + e.getMessage());
//            }
            this.setTimeLastChecked30Min(new Date());
        }
        dataClassifier = new DataClassifier(this.getPostedSpeedLimit());
        // Classify the data for events

        if (!this.getEventHasBeenDetected().containsKey("speed"))
        {
            this.getEventHasBeenDetected().put("speed", false);
        }
        if (!this.getEventHasBeenDetected().containsKey("accelerate"))
        {
            this.getEventHasBeenDetected().put("accelerate", false);
        }
        if (!this.getEventHasBeenDetected().containsKey("brake"))
        {
            this.getEventHasBeenDetected().put("brake", false);
        }
        if (!this.getEventHasBeenDetected().containsKey("turn"))
        {
            this.getEventHasBeenDetected().put("turn", false);
        }

        // Classify the data for events
        this.setEventHasBeenDetected(dataClassifier.classifyData(speed, gForce, turningRate, timestamp, networkManager, location, this.getCurrentWeather(), this.getEventHasBeenDetected()));
    }
public void RequestWeather(Location location){
    networkManager = new NetworkManager(getApplicationContext());
    try {
        // Get the weather data as a Response object
        Response weatherResponse = networkManager.getWeatherFromLocation(location);

        // Check if the response is successful
        if (!weatherResponse.isSuccessful()) {
            throw new IOException("Failed to fetch weather data. HTTP Code: " + weatherResponse.code());
        }

        // Deserialize the response body into a Weather object
        String weatherResponseBody = null;
        if (weatherResponse.body() != null) {
            weatherResponseBody = weatherResponse.body().string();
        }
        Gson gson = new Gson();
        this.setCurrentWeather(gson.fromJson(weatherResponseBody, Weather.class));

    } catch (IOException e) {
        System.err.println("Error retrieving or processing weather data: " + e.getMessage());
        throw new RuntimeException(e);
    }
}
public void RequestRoad(Location location){
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
            this.setPostedSpeedLimit(road.getSpeedLimit());
        } else {
            // Log error or handle unsuccessful response
            System.err.println("Failed to fetch road data. HTTP Code: " + response.code());
        }
    } catch (IOException e) {
        // Handle exceptions
        System.err.println("Error fetching road data: " + e.getMessage());
    }
    this.setTimeLastChecked30Min(new Date());
}

}

