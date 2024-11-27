package com.example.driveguard.activities;

import static com.example.driveguard.activities.TripScreen.getCredentials;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.example.driveguard.ButtonDeck;
import com.example.driveguard.R;
import com.example.driveguard.objects.Credentials;

public class Settings extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        preferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        boolean darkMode = preferences.getBoolean("darkMode", false);

        SwitchCompat switchCompat = findViewById(R.id.darkMode);

        //Used to retrieve the driverID and login token from the previous activity
        Bundle extras = getIntent().getExtras();
        Credentials credentials = getCredentials(extras);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButtonDeck.SetUpButtons(this, credentials);

        switchCompat.setOnCheckedChangeListener(null);
        switchCompat.setChecked(darkMode);

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) ->  {
            editor = preferences.edit();
            editor.putBoolean("darkMode", isChecked);
            editor.apply();
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
