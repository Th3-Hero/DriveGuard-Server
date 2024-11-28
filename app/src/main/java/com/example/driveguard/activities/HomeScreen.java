package com.example.driveguard.activities;

import static com.example.driveguard.GsonUtilities.JsonToWeather;
import static com.example.driveguard.Utilities.LoadCredentials;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driveguard.ButtonDeck;
import com.example.driveguard.DataCollector;
import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.objects.Credentials;
import com.example.driveguard.objects.Driver;
import com.example.driveguard.objects.Weather;
import com.squareup.picasso.Picasso;

import lombok.SneakyThrows;
import okhttp3.Response;

public class HomeScreen extends AppCompatActivity {

    private Credentials credentials;

    private ImageView weatherIcon;
    private TextView welcomeMessage;
    private TextView username;
    private TextView score;
    private NetworkManager networkManager;
    private DataCollector dataCollector;
    private Weather weather;
    private Driver driver;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //allows the UI thread to perform network calls. We could make them async if this causes issues
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dataCollector = new DataCollector(getApplicationContext());
        networkManager = new NetworkManager(getApplicationContext());

        Response response = networkManager.getWeatherFromLocation(dataCollector.getStartingLocation());

        if (response.isSuccessful()){
            assert response.body() != null;
            weather = JsonToWeather(response.body().string());
            weatherIcon = findViewById(R.id.weather);
            Picasso.get()
                    .load(weather.getIconUrl())
                    .into(weatherIcon);
        }


        boolean darkMode = false;
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        darkMode = preferences.getBoolean("darkMode", false);

        if (darkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }


        //Used to retrieve the driverID and login token from the previous activity

        //if there were no credentials received from the previous activity we then try retrieve them from the file

        credentials = LoadCredentials(getApplicationContext());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButtonDeck.SetUpButtons(HomeScreen.this);

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
            Intent intent = new Intent(HomeScreen.this, Settings.class);
            startActivity(intent);
        }
        else if (id == R.id.profile){
            Intent intent;
            intent = new Intent(HomeScreen.this, ProfileScreen.class);
            startActivity(intent);
        }
        return true;
    }

}