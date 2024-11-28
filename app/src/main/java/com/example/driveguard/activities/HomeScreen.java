package com.example.driveguard.activities;

import static com.example.driveguard.GsonUtilities.JsonToWeather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.example.driveguard.GsonUtilities;
import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.objects.Driver;
import com.example.driveguard.objects.Weather;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;

import lombok.SneakyThrows;
import okhttp3.Response;

public class HomeScreen extends AppCompatActivity {

    private ImageView weatherIcon;
    private TextView welcomeMessage;
    private TextView username;
    private TextView score;
    private TextView scoreMessage;
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

        LoadWeatherIcon(networkManager);
        LoadTimeMessage();
        LoadDriver(networkManager);

        boolean darkMode = false;
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        darkMode = preferences.getBoolean("darkMode", false);

        if (darkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

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
    @SneakyThrows
    public void LoadWeatherIcon(@NonNull NetworkManager networkManager){
        Response weatherRes = networkManager.getWeatherFromLocation(dataCollector.getStartingLocation());
        if (weatherRes.isSuccessful()){
            assert weatherRes.body() != null;
            weather = JsonToWeather(weatherRes.body().string());
            weatherIcon = findViewById(R.id.weather);
            Picasso.get()
                    .load(weather.getIconUrl())
                    .placeholder(R.drawable.icon_sun)
                    .into(weatherIcon);
        }
    }
    public void LoadTimeMessage(){
        welcomeMessage = findViewById(R.id.greeting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime current = LocalDateTime.now();
            if (current.getHour() < 4){
                welcomeMessage.setText(R.string.good_evening);
            } else if (current.getHour() < 12) {
                welcomeMessage.setText(R.string.good_morning);
            } else if (current.getHour() < 18) {
                welcomeMessage.setText(R.string.good_afternoon);
            } else {
                welcomeMessage.setText(R.string.good_evening);
            }
        } else{
            welcomeMessage.setText(R.string.good_day);
        }
    }
    @SneakyThrows
    public void LoadDriver(@NonNull NetworkManager networkManager){
        Response response = networkManager.getDriver();
        username = findViewById(R.id.username);
        score = findViewById(R.id.score);
        scoreMessage = findViewById(R.id.score_message);
        if (response!= null && response.isSuccessful()){
            assert response.body() != null;
            Driver driver = GsonUtilities.JsonToDriver(response.body().string());
            if (driver != null){
                username.setText(driver.getUsername());
                score.setText(String.valueOf(driver.getOverallScore()));
            }
        }
        else {
            username.setText(R.string.guest);
            score.setVisibility(View.INVISIBLE);
            scoreMessage.setVisibility(View.INVISIBLE);
        }
    }
}