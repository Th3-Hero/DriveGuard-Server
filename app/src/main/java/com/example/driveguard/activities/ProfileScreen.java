package com.example.driveguard.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.driveguard.ButtonDeck;
import com.example.driveguard.GsonUtilities;
import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.Utilities;
import com.example.driveguard.objects.Credentials;
import com.example.driveguard.objects.Driver;

import java.util.Objects;

import lombok.SneakyThrows;
import okhttp3.Response;
public class ProfileScreen extends AppCompatActivity {
    private Driver driver;
    private TextView usernameText;
    private Button loginButton, logoutButton, signupButton;
    private NetworkManager networkManager;
    private Credentials credentials;

    @SneakyThrows
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);

        Credentials credentials = Utilities.LoadCredentials(getApplicationContext());

        networkManager = new NetworkManager(getApplicationContext());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ButtonDeck.SetUpButtons(ProfileScreen.this);

        usernameText = findViewById(R.id.usernameText);
        loginButton = findViewById(R.id.loginButton);
        logoutButton = findViewById(R.id.logoutButton);
        signupButton = findViewById(R.id.signupButton);

        Response driverResponse;
        if (credentials.getDriverId() != -1){
            driverResponse = networkManager.getDriver();

            if (driverResponse.isSuccessful()){
                assert driverResponse.body() != null;
                driver = GsonUtilities.JsonToDriver(driverResponse.body().string());
            }
        }

        // Checking credentials
        if(!Objects.equals(credentials.getToken(), "") && credentials.getDriverId() != -1){

            usernameText.setText(driver.getUsername());
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            signupButton.setVisibility(View.GONE);
        } else {

            //currentCredentials = null;
            usernameText.setText("Guest");
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            signupButton.setVisibility(View.VISIBLE);
        }

        loginButton.setOnClickListener(v -> navigateToLogin());
        logoutButton.setOnClickListener(v -> performLogout(credentials));
        signupButton.setOnClickListener(v -> navigateToSignUp());
    }

    private void navigateToLogin() {

        Intent intent = new Intent(ProfileScreen.this, LoginScreen.class);
        startActivity(intent);

    }

    private void navigateToSignUp(){
        Intent intent = new Intent(ProfileScreen.this, SignupScreen.class);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void performLogout(Credentials credentials) {

        if(credentials.getDriverId() == -1) {

            Toast.makeText(this, "No active session to logout from.", Toast.LENGTH_SHORT).show();
            return;

        }

        Response response = networkManager.Logout();

            //Toast.makeText(this, "An error occured: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        if(response.isSuccessful()) {

            Utilities.DeleteCredentials(getApplicationContext());
            Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show();
            usernameText.setText("Guest");
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            signupButton.setVisibility(View.VISIBLE);

        } else {

            Toast.makeText(this, "Logout failed: " + response.message(), Toast.LENGTH_SHORT).show();

        }
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
