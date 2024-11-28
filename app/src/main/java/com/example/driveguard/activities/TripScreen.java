package com.example.driveguard.activities;

import static com.example.driveguard.GsonUtilities.JsonToTrip;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.example.driveguard.ButtonDeck;
import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.objects.Credentials;
import com.example.driveguard.objects.Trip;

import java.io.IOException;

import okhttp3.Response;
import com.example.driveguard.DataCollector;

public class TripScreen extends AppCompatActivity {

    private DataCollector dataCollector;
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

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonDeck.ToggleButtons(TripScreen.this);

                NetworkManager networkManager = new NetworkManager();

                //start trip here
                if (startButton.isChecked()){//for starting trip
                    // 201 means a trip was started successfully
                    Response response = networkManager.StartTrip(credentials, dataCollector.getStartingLocation());
                    if (response != null && response.code() == START_TRIP_SUCCESS){
                        //networkManager.dataCollector.startDataCollection();
                        Toast.makeText(TripScreen.this, "Trip Successfully started!", Toast.LENGTH_LONG).show();

                        try {
                            assert response.body() != null;
                            currentTrip =  JsonToTrip(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //retrieve the trip ID sent by server
                            credentials.setTripId(currentTrip.getId());


                        //BROOKE you can add all your trip stuff here

//                        run an async loop to check for events every 25 millisecond
//                        look at timeline from javafx

                    }
                    else{//set button back to unchecked

                        startButton.setChecked(false);
                    }
                }

                else if(!startButton.isChecked()){
                    Response response = networkManager.EndTrip(credentials, dataCollector.getStartingLocation());
                    if (response != null && response.code() == STOP_TRIP_SUCCESS){
                        //networkManager.dataCollector.stopDataCollection();
                        Toast.makeText(TripScreen.this, "Trip successfully ended!", Toast.LENGTH_LONG).show();

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

    }

