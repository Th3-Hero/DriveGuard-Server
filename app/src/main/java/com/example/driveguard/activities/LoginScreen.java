package com.example.driveguard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.objects.Account;
import com.example.driveguard.objects.Credentials;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.Objects;

import lombok.SneakyThrows;
import okhttp3.Response;

public class LoginScreen extends AppCompatActivity {
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        networkManager = new NetworkManager();

        Button loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @SneakyThrows
            @Override
            public void onClick(View v) {

                String username = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextUsernameLogin)).getText()).toString();
                String password = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextPasswordLogin)).getText()).toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginScreen.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(LoginScreen.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Account account = new Account(null, null, username, password);

                loginButton.setEnabled(false);

                Response response;

                try {

                   response = networkManager.Login(account);

                } catch (Exception e){

                    Toast.makeText(LoginScreen.this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    throw new Exception(e);
                } finally {

                    loginButton.setEnabled(true);

                }

                if (response.isSuccessful()) {
                    Toast.makeText(LoginScreen.this, "Login successful.", Toast.LENGTH_SHORT).show();

                    Gson gson = new Gson();
                    String responseBody = response.body().string();

                    Credentials credentials = gson.fromJson(responseBody, Credentials.class);

                    Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
                    intent.putExtra("driverID", credentials.getDriverId());
                    intent.putExtra("token", credentials.getToken());
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(LoginScreen.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

}
