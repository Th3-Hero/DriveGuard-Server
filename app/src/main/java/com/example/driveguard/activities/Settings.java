package com.example.driveguard.activities;

import static com.example.driveguard.Utilities.getCredentialsFromExtras;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
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
        SwitchCompat switchCompat = findViewById(R.id.darkMode);
        //Used to retrieve the driverID and login token from the previous activity

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButtonDeck.SetUpButtons(this);

        switchCompat.setOnCheckedChangeListener(null);
        boolean darkMode = preferences.getBoolean("darkMode", false);
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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        if (id == R.id.settings){
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
        else if (id == R.id.profile){
            Intent intent;
            intent = new Intent(this, ProfileScreen.class);
            startActivity(intent);
        }
        return true;
    }
}
