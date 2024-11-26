package com.example.driveguard.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.objects.Account;
import com.example.driveguard.objects.Credentials;

import okhttp3.Response;
public class ProfileScreen extends AppCompatActivity {
    private TextView usernameText;
    private Button loginButton, logoutButton;
    private NetworkManager networkManager;
    private Credentials currentCredentials;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);

        networkManager = new NetworkManager();

        usernameText = findViewById(R.id.usernameText);
        loginButton = findViewById(R.id.loginButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Checking credentials

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String token = intent.getStringExtra("token");
        int driverId = intent.getIntExtra("driverID", -1);

        if(username != null && token != null && driverId != -1){

            currentCredentials = new Credentials(driverId, token, -1);
            usernameText.setText(username);
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);

        } else {

            currentCredentials = null;
            usernameText.setText("Guest");
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);

        }

        loginButton.setOnClickListener(v -> navigateToLogin());
        loginButton.setOnClickListener(v -> performLogout());

    }

    private void navigateToLogin() {

        Intent intent = new Intent(ProfileScreen.this, LoginScreen.class);
        startActivity(intent);

    }

    @SuppressLint("SetTextI18n")
    private void performLogout() {

        if(currentCredentials == null) {

            Toast.makeText(this, "No active session to logout from.", Toast.LENGTH_SHORT).show();
            return;

        }

        Response response = null;

        try {

            response = networkManager.Logout(currentCredentials);

            if(response.isSuccessful()) {

                currentCredentials = null;
                Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show();
                usernameText.setText("Guest");
                loginButton.setVisibility(View.VISIBLE);
                logoutButton.setVisibility(View.GONE);

            } else {

                Toast.makeText(this, "Logout failed: " + response.message(), Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e){

            Toast.makeText(this, "An error occured: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

}