package com.example.driveguard.activities;

import static com.example.driveguard.activities.TripScreen.getCredentials;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.driveguard.R;
import com.example.driveguard.objects.Credentials;
import com.squareup.picasso.Picasso;

public class HomeScreen extends AppCompatActivity {

    private Credentials credentials;

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

        ImageView imageView = findViewById(R.id.weather);
        Picasso.get()
                .load("https://openweathermap.org/img/wn/10d@2x.png")
                .into(imageView);

        boolean darkMode = false;
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        darkMode = preferences.getBoolean("darkMode", false);

        if (darkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        //Used to retrieve the driverID and login token from the previous activity
        Bundle extras = getIntent().getExtras();
        credentials = getCredentials(extras);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButtonDeck.SetUpButtons(HomeScreen.this, credentials);

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
            /*if there were no credentials retrieved we assume the user is logged out and send them
            to the sign up page*/
            if (credentials == null) {
                intent = new Intent(HomeScreen.this, SignupScreen.class);
            } else {//if there are credentials we send the user to the profile page
                intent = new Intent(HomeScreen.this, ProfileScreen.class);
                intent.putExtra("driverID", credentials.getDriverId());
                intent.putExtra("token", credentials.getToken());
            }
            startActivity(intent);
        }
        return true;
    }

}