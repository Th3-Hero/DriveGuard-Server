package com.example.driveguard.activities;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.driveguard.ButtonDeck;
import com.example.driveguard.R;
import com.example.driveguard.Utilities;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class Settings extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean isInitializing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_settings);

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
        MenuItem item = menu.findItem(R.id.settings);

        if (item != null && item.getIcon() != null){
            Drawable icon = DrawableCompat.wrap(item.getIcon());

            DrawableCompat.setTint(icon, getResources().getColor(R.color.darkGrey));
            item.setIcon(icon);
        }
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
