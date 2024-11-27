package com.example.driveguard;

import com.example.driveguard.objects.Credentials;
import com.example.driveguard.objects.DrivingEvent;
import com.example.driveguard.objects.EventSeverity;
import com.example.driveguard.objects.EventType;
import com.example.driveguard.objects.HardAccelerateEvent;
import com.example.driveguard.objects.HardBrakeEvent;
import com.example.driveguard.objects.HardCorneringEvent;
import com.example.driveguard.objects.ServerLocation;
import com.example.driveguard.objects.SpeedingEvent;
import com.example.driveguard.objects.Weather;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Response;

/* Class Name: DataClassifier
 * Class Author: Brooke Cronin
 * Date: November 14, 2024
 * Course: SENG71000
 *
 * Description: This class is responsible for classifying driving events based on data
 *              inputs such as speed, g-force, and turning rate, and categorizing them
 *              into speed, acceleration, braking, and turning events.
 */
public class DataClassifier
{

    private final float postedSpeedLimit;

    /* Method Name: DataClassifier
     * Method Author: Brooke Cronin
     * Description: Constructor for the DataClassifier class. Initializes event lists and sets posted speed limit.
     * Parameters: float postedSpeedLimit (the speed limit to be used for speed classification)
     * Returns: N/A
     */
    public DataClassifier(float postedSpeedLimit)
    {
        this.postedSpeedLimit = postedSpeedLimit;
    }

    /* Method Name: classifyData
     * Method Author: Brooke Cronin
     * Description: Classifies data based on speed, g-force, and turning rate. Adds events to respective lists if criteria are met.
     * Parameters: float speed (current speed), float gForce (current g-force), float turningRate (current turning rate),
     *             long timestamp (time of event)
     * Returns: N/A
     */
    public void classifyData(float speed, float gForce, float turningRate, String timestamp, NetworkManager networkManager, Credentials credentials, android.location.Location location)
    {
        try {
            // Get the weather data as a Response object
            Response response = networkManager.getWeatherFromLocation(location);

            // Check if the response is successful
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch weather data. HTTP Code: " + response.code());
            }

            // Deserialize the response body into a Weather object
            String responseBody = null;
            if (response.body() != null) {
                responseBody = response.body().string();
            }
            Gson gson = new Gson();
            Weather currentWeather = gson.fromJson(responseBody, Weather.class);

            // Classify speed event
            if (speed > this.postedSpeedLimit) {
                SpeedingEvent speedEvent = new SpeedingEvent(speed, timestamp, location, networkManager, currentWeather);
                if (speedEvent.isSpeeding()) {
                    networkManager.addEventToTrip(
                            new DrivingEvent(
                                    timestamp,
                                    new ServerLocation(location.getLatitude(), location.getLongitude()),
                                    EventType.SPEEDING,
                                    EventSeverity.LOW,
                                    speedEvent.deductPoints()
                            ),
                            credentials
                    );
                }
                // Classify Acceleration Event
                HardAccelerateEvent accelerateEvent = new HardAccelerateEvent(gForce, timestamp, location, currentWeather);
                if (accelerateEvent.isHarshAcceleration())
                {
                    networkManager.addEventToTrip(
                            new DrivingEvent(
                                    timestamp,
                                    new ServerLocation(location.getLatitude(), location.getLongitude()),
                                    EventType.HARD_ACCELERATION,
                                    EventSeverity.LOW,
                                    accelerateEvent.deductPoints()
                            ),
                            credentials
                    );
                }

                // Classify Braking Event
                HardBrakeEvent brakeEvent = new HardBrakeEvent(gForce, timestamp, location, currentWeather);
                if (brakeEvent.isHarshBraking())
                {
                    networkManager.addEventToTrip(
                            new DrivingEvent(
                                    timestamp,
                                    new ServerLocation(location.getLatitude(), location.getLongitude()),
                                    EventType.HARD_BRAKING,
                                    EventSeverity.LOW,
                                    brakeEvent.deductPoints()
                            ),
                            credentials
                    );
                }

                // Classify Turning Event
                HardCorneringEvent turningEvent = new HardCorneringEvent(turningRate, timestamp, location, currentWeather);
                if (turningEvent.isAggressiveTurn())
                {
                    networkManager.addEventToTrip(
                            new DrivingEvent(
                                    timestamp,
                                    new ServerLocation(location.getLatitude(), location.getLongitude()),
                                    EventType.HARD_CORNERING,
                                    EventSeverity.LOW,
                                    turningEvent.deductPoints()
                            ),
                            credentials
                    );
                }
            }

            // Add other classifications as needed (acceleration, braking, turning)

        } catch (IOException e) {
            System.err.println("Error retrieving or processing weather data: " + e.getMessage());
            throw new RuntimeException(e);

        }


    }
}
