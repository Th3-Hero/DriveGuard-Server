package com.example.driveguard;

import android.app.Activity;

import com.example.driveguard.objects.Trip;
import com.google.gson.Gson;


import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import trip_data.DataCollector;

public class NetworkManager {
    private OkHttpClient client;
    public DataCollector dataCollector;
    private final String scheme = "http";
    private final String baseUrl = "drive-guard-api.the-hero.dev";
    private final String tripUrl = "trip";
    private final String authUrl = "auth";
    private final String driverUrl = "driver";
    private final String drivingContextUrl = "driving-context";

    public NetworkManager(){
        client = new OkHttpClient();
    }
    public Response StartTrip(int driverID, int token, Activity activity) {
        dataCollector = new DataCollector(activity);

        Gson gson = new Gson();
        String jsonBody = gson.toJson(dataCollector.getStartingLocation()); // give gson the current location
        //url for the request
        HttpUrl url = new HttpUrl.Builder()
                .scheme(scheme)
                .host(baseUrl)
                .addPathSegment(tripUrl)
                .addQueryParameter("token", String.valueOf(token))
                .build();
        //building the request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "*/*")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();

        //Synchronous request to server
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            //possibly display trip details but for now just check that trip has been started
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response EndTrip(int driverID, int tripID, int token ){
        Gson gson = new Gson();
        String jsonBody = gson.toJson(dataCollector.getStartingLocation());

        HttpUrl url =  new HttpUrl.Builder()
                .scheme(scheme)
                .host(baseUrl)
                .addPathSegment(tripUrl)
                .addPathSegments(String.valueOf(driverID))
                .addPathSegments(String.valueOf(tripID))
                .addQueryParameter("token", String.valueOf(token))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "*/*")
                .addHeader("Content-Type", "application/json")
                .patch(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();

        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getTripSummary(int driverID, int tripID, int token){

    }

    public void SignUp(){

    }
    public void Login(){

    }
    public Trip ResponseToTrip(String response){//add error handling
        Gson gson = new Gson();
        return gson.fromJson(response, Trip.class);
    }
}
