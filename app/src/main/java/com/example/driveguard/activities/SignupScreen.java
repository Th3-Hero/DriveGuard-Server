package com.example.driveguard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.driveguard.NetworkManager;
import com.example.driveguard.objects.Account;
import com.example.driveguard.objects.Credentials;
import com.google.android.material.textfield.TextInputEditText;
import com.example.driveguard.R;
import com.google.gson.Gson;

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
        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String firstName = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextFirstName)).getText()).toString();
                String lastName = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextLastName)).getText()).toString();
                String username = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextUsername)).getText()).toString();
                String password = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextPassword)).getText()).toString();
                String rePassword = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.editTextRePassword)).getText()).toString();

                if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                        TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupScreen.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(rePassword)) {
                    Toast.makeText(SignupScreen.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Account account = new Account(firstName, lastName, username, password);

                signUpButton.setEnabled(false);
                Response response = null;

                try {

                    networkManager.SignUp(account);

                } catch (Exception e) {

                    Toast.makeText(SignupScreen.this, "An error occurred: " + response.code(), Toast.LENGTH_LONG).show();

                } finally {

                    signUpButton.setEnabled(true);

                }

                if (response.isSuccessful()) {

                    Toast.makeText(SignupScreen.this, "Sign up successful.", Toast.LENGTH_SHORT).show();

                    Response loginResponse;

                    try {
                        loginResponse = networkManager.Login(account);
                    } catch (RuntimeException e) {
                        throw new RuntimeException(e);
                    }

                    if (loginResponse.isSuccessful()){
                        Toast.makeText(SignupScreen.this, "Log in successful.", Toast.LENGTH_SHORT).show();

                        Gson gson = new Gson();
                        Credentials credentials = gson.fromJson(loginResponse.body().toString(), Credentials.class);

                        Intent intent = new Intent(SignupScreen.this, LoginScreen.class);
                        intent.putExtra("driverID", credentials.getDriverID());
                        intent.putExtra("token", credentials.getToken());
                        startActivity(intent);
                        finish();
                    }

                } else {

                    Toast.makeText(SignupScreen.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();

                }

            }

        });

    }

}
