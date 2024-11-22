package com.example.driveguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import trip_data.DataCollector;

public class TripScreen extends AppCompatActivity {

    private DataCollector dataCollector;
    int driverID;
    int token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_screen);

        //Used to retrieve the driverID and login token from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            driverID = extras.getInt("driverID");
            token = extras.getInt("token");
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

                OkHttpClient client = new OkHttpClient();

                //start trip here
                if (!startButton.isChecked()){//for starting trip
                    //String serverResponse = RequestStartTrip(client);
                    //System.out.println(serverResponse);
                }
                else if(startButton.isChecked()){

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

