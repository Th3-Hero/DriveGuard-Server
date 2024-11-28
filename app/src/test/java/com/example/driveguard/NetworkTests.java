package com.example.driveguard;

import static com.example.driveguard.GsonUtilities.JsonToCredentials;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import android.app.Activity;

import com.example.driveguard.objects.Account;
import com.example.driveguard.objects.Credentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.io.IOException;

import okhttp3.Response;

public class NetworkTests {


    @Test
    public void TestSignUp() throws IOException {
        Account account = new Account("bob", "burger", "helloo", "Password!5");

        NetworkManager networkManager = new NetworkManager();

        Response response = networkManager.SignUp(account);

        //Gson gson = new Gson();
        //ErrorReport errorReport = gson.fromJson(response.body().string(), ErrorReport.class);

        System.out.println(response.code());
        System.out.println(response.body().string());

        assertTrue(response.isSuccessful());

    }

    @Test
    public void TestLogin() throws IOException {
        Account account = new Account("bob", "burger", "helloo", "Password!5");

        NetworkManager networkManager = new NetworkManager();

        Response response = networkManager.Login(account);

        System.out.println(response.code());
        System.out.println(response.body().string());

        assertTrue(response.isSuccessful());
    }
    @Test
    public void TestLogout() throws IOException{
        Account account = new Account("bob", "burger", "helloo", "Password!5");

        NetworkManager networkManager = new NetworkManager();

        Response response = networkManager.Login(account);

        Credentials credentials = JsonToCredentials(response.body().string());

        if (response.isSuccessful()){
            Response logoutResponse = networkManager.Logout(credentials);

            assertTrue(logoutResponse.isSuccessful());
        }
    }
    @Test
    public void TestStartTrip() throws IOException {
        Account account = new Account("bob", "burger", "helloo", "Password!5");

        Activity activity = new Activity();

        NetworkManager networkManager = new NetworkManager();

        Response response = networkManager.Login(account);

        DataCollector dataCollector = new DataCollector(activity.getApplicationContext());

        if(response.isSuccessful()){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Credentials credentials = gson.fromJson(response.body().string(), Credentials.class);

            Response response1 = networkManager.StartTrip(credentials, dataCollector.getStartingLocation());

            System.out.println(response1.code());
            System.out.println(response1.body().string());

            assertTrue(response1.isSuccessful());
        }


    }
}
