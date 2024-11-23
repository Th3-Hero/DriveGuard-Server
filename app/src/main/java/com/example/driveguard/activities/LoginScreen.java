package com.example.driveguard.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.driveguard.NetworkManager;
import com.example.driveguard.R;
import com.example.driveguard.objects.Account;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Response;

public class LoginScreen extends AppCompatActivity {

    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        networkManager = new NetworkManager();

        Button loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(v -> {

            String username = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextUsernameLogin)).getText()).toString();
            String password = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextPasswordLogin)).getText()).toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            Account account = new Account(null, null, username, password);

            loginButton.setEnabled(false);

            try {

                Response response = networkManager.Login(account);

                if (response.isSuccessful()) {
                    Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e){

                Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();

            } finally {

                loginButton.setEnabled(true);

            }

        });

    }

}
