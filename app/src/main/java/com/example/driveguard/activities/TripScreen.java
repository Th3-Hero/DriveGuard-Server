package com.example.driveguard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.driveguard.ButtonDeck;
import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.objects.Credentials;
import com.example.driveguard.objects.Trip;

import java.io.IOException;

import okhttp3.Response;
import trip_data.DataCollector;

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

        //Used to retrieve the driverID and login token from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            int driverID = extras.getInt("driverID");
            String token = extras.getString("token");
            credentials = new Credentials(driverID, token);
        }

        //defines the toolbar used in the activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toggle button that is used to stop and start trips
        final ToggleButton startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonDeck.ToggleButtons(TripScreen.this);

                NetworkManager networkManager = new NetworkManager();

                //start trip here
                if (!startButton.isChecked()){//for starting trip
                    // 201 means a trip was started successfully
                    Response response = networkManager.StartTrip(credentials, TripScreen.this);
                    if (response != null && response.code() == START_TRIP_SUCCESS){
                        //more will probably be needed here
                        //networkManager.dataCollector.startDataCollection();

                        try {
                            assert response.body() != null;
                            currentTrip =  networkManager.JsonToTrip(response.body().string());
                            //retrieve the trip ID sent by server
                            credentials.setTripID(currentTrip.getId());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else{//set button back to unchecked. Add error message later
                        startButton.setChecked(false);
                    }
                }

                else if(startButton.isChecked()){
                    Response response = networkManager.EndTrip(credentials);
                    if (response != null && response.code() == STOP_TRIP_SUCCESS){
                        networkManager.dataCollector.stopDataCollection();

                        //display trip summary here which is withing response
                    }
                }

            }
        });

        ButtonDeck.SetUpButtons(this);
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
        return true;
    }


    }

