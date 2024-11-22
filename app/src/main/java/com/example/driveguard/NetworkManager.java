package com.example.driveguard;

import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import trip_data.DataCollector;

public class NetworkManager {
    private OkHttpClient client;
    private DataCollector dataCollector;
private String baseUrl = "http://drive-guard-api.the-hero.dev/";
    public String StartTrip(int driverID, int token) {
        //dataCollector = new DataCollector(TripScreen.this);

        Gson gson = new Gson();
        //String jsonBody = gson.toJson(); // give gson the current location
        Request request = new Request.Builder()
                .url((baseUrl + driverID + "?token=" + token))
                .addHeader("accept", "*/*")
                .addHeader("Content-Type", "application/json")
                .post()
                .build();

        //Synchronous request to server
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String EndTrip(int driverID, int tripID, int token ){
        Gson gson = new Gson();
        //String jsonBody = gson.toJson();
        Request request = new Request.Builder()
                .url(("http://drive-guard-api.the-hero.dev/trip/" + driverID + "?token=" + token))
                .addHeader("accept", "*/*")
                .addHeader("Content-Type", "application/json")
                .post()
                .build();
    }

    public void SignUp(){

    }
    public void Login(){

    }
}
