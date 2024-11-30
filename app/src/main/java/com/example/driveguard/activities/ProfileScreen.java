package com.example.driveguard.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

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
    private Button loginButton, logoutButton, signupButton, changeUsernameButton, changePasswordButton;
    private NetworkManager networkManager;
    private Credentials credentials;

    @SneakyThrows
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_profile);

        networkManager = new NetworkManager(getApplicationContext());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ButtonDeck.SetUpButtons(ProfileScreen.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameText = findViewById(R.id.usernameText);
        loginButton = findViewById(R.id.loginButton);
        logoutButton = findViewById(R.id.logoutButton);
        signupButton = findViewById(R.id.signupButton);

        changeUsernameButton = findViewById(R.id.changeUsernameButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        Response driverResponse = networkManager.getDriver();

        if (driverResponse != null && driverResponse.isSuccessful()){
            assert driverResponse.body() != null;
            driver = GsonUtilities.JsonToDriver(driverResponse.body().string());
        } else {
            driver = new Driver();
        }

        // Checking credentials
        if(!Objects.equals(driver.getUsername(), "") && driver.getId() != -1){

            usernameText.setText(driver.getUsername());
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            signupButton.setVisibility(View.GONE);
            changeUsernameButton.setVisibility(View.VISIBLE);
            changePasswordButton.setVisibility(View.VISIBLE);
            changeUsernameButton.setOnClickListener(v -> showChangeUsernameDialog());
            changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());

        } else {
            usernameText.setText("Guest");
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            signupButton.setVisibility(View.VISIBLE);
            changeUsernameButton.setVisibility(View.GONE);
            changePasswordButton.setVisibility(View.GONE);
        }

        loginButton.setOnClickListener(v -> navigateToLogin());
        logoutButton.setOnClickListener(v -> performLogout(driver));
        signupButton.setOnClickListener(v -> navigateToSignUp());



    }

    private void showChangeUsernameDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_change_username, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        TextView newUsernameInput = view.findViewById(R.id.newUsernameInput);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button okButton = view.findViewById(R.id.okButton);

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        okButton.setOnClickListener(v -> {

            String newUsername = newUsernameInput.getText().toString().trim();
            if (newUsername.isEmpty()) {

                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;

            }

            Response response = networkManager.UpdateUsername(newUsername);

            if(response.isSuccessful()) {
                Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                usernameText.setText(newUsername);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Failed to update username: " + response.message(), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void showChangePasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        TextView oldPasswordInput = view.findViewById(R.id.oldPasswordInput);
        TextView newPasswordInput = view.findViewById(R.id.newPasswordInput);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button okButton = view.findViewById(R.id.okButton);

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        okButton.setOnClickListener(v -> {

            String oldPassword = oldPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();

            if(oldPassword.isEmpty() || newPassword.isEmpty()) {

                Toast.makeText(this, "Passwords cannot be empty", Toast.LENGTH_SHORT).show();
                return;

            }

            Response response = networkManager.UpdatePassword(oldPassword, newPassword);

            if(response.isSuccessful()) {

                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            } else {

                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();

            }

        });

        dialog.show();

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
    private void performLogout(Driver driver) {

        if (driver.getId() == -1) {

            Toast.makeText(this, "No active session to logout from.", Toast.LENGTH_SHORT).show();
            return;

        }

        Response response = networkManager.Logout();

        if(response.isSuccessful()) {

            Utilities.DeleteCredentials(getApplicationContext());
            Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show();
            usernameText.setText("Guest");
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            signupButton.setVisibility(View.VISIBLE);

        } else {

            Toast.makeText(this, "System Error", Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        MenuItem item = menu.findItem(R.id.profile);

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
