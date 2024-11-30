package com.example.driveguard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.driveguard.objects.Credentials;

public class Utilities {
    public static Credentials getCredentialsFromExtras(Bundle extras){
        if (extras != null){
            int driverID = extras.getInt("driverID");
            String token = extras.getString("token");
            return new Credentials(driverID, token);
        } else {
            return null;
        }
    }
    public static Credentials LoadCredentials(@NonNull Context context){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return new Credentials(preferences.getInt("driverId", -1), preferences.getString("token", ""), preferences.getInt("tripId", -1));
    }
    public static void SaveCredentials(@NonNull Context context, @NonNull Credentials credentials){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preferences_file), Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("driverId", credentials.getDriverId());
        editor.putString("token", credentials.getToken());
        if (credentials.getTripId() != -1){
            editor.putInt("tripId", credentials.getTripId());
        }
        editor.apply();
    }
    public static void SaveTripID(@NonNull Context context, int tripId){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("tripId", tripId);
        editor.apply();
    }
    public static void DeleteCredentials(@NonNull Context context){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("driverId");
        editor.remove("token");
        editor.remove("tripId");
        editor.apply();
    }
    public static void applyDarkModeIfNeeded(SharedPreferences preferences) {
        boolean darkMode = preferences.getBoolean("darkMode", false);
        int currentMode = AppCompatDelegate.getDefaultNightMode();

        if (darkMode && currentMode != AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (!darkMode && currentMode != AppCompatDelegate.MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
