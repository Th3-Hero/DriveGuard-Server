package com.example.driveguard.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.driveguard.NetworkManager;
import com.example.driveguard.objects.Account;
import com.google.android.material.textfield.TextInputEditText;
import com.example.driveguard.R;

import java.util.Objects;

import okhttp3.Response;

public class SignupScreen extends AppCompatActivity {

    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        networkManager = new NetworkManager();

        Button signUpButton = findViewById(R.id.buttonSignUp);
        signUpButton.setOnClickListener(v -> {

            String firstName = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextFirstName)).getText()).toString();
            String lastName = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextLastName)).getText()).toString();
            String username = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextUsername)).getText()).toString();
            String password = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextPassword)).getText()).toString();
            String rePassword = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextRePassword)).getText()).toString();

            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                    TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(rePassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            Account account = new Account(firstName, lastName, username, password);

            signUpButton.setEnabled(false);

            try {

                Response response = networkManager.SignUp(account);
                if (response.isSuccessful()) {

                    Toast.makeText(this, "Sign up successful.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignupScreen.this, LoginScreen.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();

                }

            } catch (Exception e) {

                Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();

            } finally {

                signUpButton.setEnabled(true);

            }

        });

    }

}
