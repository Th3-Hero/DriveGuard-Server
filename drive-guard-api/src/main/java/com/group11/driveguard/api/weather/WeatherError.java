package com.group11.driveguard.api.weather;

public record WeatherError(
    Boolean error,
    String reason
) { }
