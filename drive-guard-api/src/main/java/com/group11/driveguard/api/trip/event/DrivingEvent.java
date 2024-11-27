package com.group11.driveguard.api.trip.event;

import com.group11.driveguard.api.map.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

import java.time.LocalDateTime;

@Schema(
    name = "DrivingEvent",
    description = "Represents an event that occurred during a trip."
)
public record DrivingEvent(
    @Schema(description = "The time the event occurred.")
    @NonNull
    LocalDateTime eventTime,

    @Schema(description = "The location of the event.")
    @NonNull
    Location location,

    @Schema(description = "The type of the event.")
    @NonNull
    EventType eventType,

    @Schema(description = "The severity of the event.")
    @NonNull
    EventType.Severity severity,

    @Schema(description = "The weather at the time and location of the event.")
    @NonNull
    WeatherType weatherType,

    @Schema(description = "The severity of the weather.")
    @NonNull
    WeatherType.WeatherSeverity weatherSeverity,

    @Schema(description = "The number of points deducted from the driver's score for the infraction.")
    @NonNull
    Integer pointsDeducted
) { }
