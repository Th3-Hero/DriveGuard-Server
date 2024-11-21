package com.group11.driveguard.api.trip.event;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum WeatherType {
    CLEAR(List.of(0, 1, 2, 3)),
    FOG(List.of(45, 48)),
    DRIZZLE(List.of(51, 53, 55)),
    FREEZING_DRIZZLE(List.of(56, 57)),
    RAIN(List.of(61, 63, 65, 80, 81, 82)),
    FREEZING_RAIN(List.of(66, 67)),
    SNOW(List.of(71, 73, 75, 77, 85, 86)),
    THUNDERSTORM(List.of(95, 96, 99)),
    UNKNOWN(List.of(-1));

    private final List<Integer> weatherCodes;

    public static WeatherType fromCode(int code) {
        return Arrays.stream(WeatherType.values())
            .filter(weatherType -> weatherType.weatherCodes.contains(code))
            .findFirst()
            .orElse(UNKNOWN);
    }

    @AllArgsConstructor
    public enum WeatherSeverity {
        CLEAR(List.of(0, 1, 2, 3)),
        LIGHT(List.of(45, 51, 53, 61)),
        MODERATE(List.of(48, 55, 56, 63, 66, 71, 73, 77, 80, 81, 85, 95)),
        HEAVY(List.of(57, 65, 67, 75, 82, 86, 96, 99)),
        UNKNOWN(List.of(-1));

        private final List<Integer> weatherCodes;

        public static WeatherSeverity fromCode(int code) {
            return Arrays.stream(WeatherSeverity.values())
                .filter(weatherSeverity -> weatherSeverity.weatherCodes.contains(code))
                .findFirst()
                .orElse(UNKNOWN);
        }
    }

}
