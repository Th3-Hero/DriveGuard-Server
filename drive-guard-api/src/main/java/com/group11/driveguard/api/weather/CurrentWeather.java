package com.group11.driveguard.api.weather;

import com.fasterxml.jackson.annotation.JsonAlias;

public record CurrentWeather(
    Double latitude,
    Double longitude,
    Current current
) {
    public record Current(
        String time,
        @JsonAlias("is_day") Boolean isDay,
        @JsonAlias("weather_code") Integer weatherCode
    ) { }
}
