package com.example.driveguard.objects;

import java.io.Serializable;

public class Weather implements Serializable {
    private String timeStamp;
    private boolean isDay;
    private WeatherType weatherType;
    private WeatherSeverity weatherSeverity;
}
