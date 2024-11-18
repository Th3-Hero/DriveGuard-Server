package com.group11.driveguard.api.trip.event;

import com.group11.driveguard.api.map.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(
    name = "DrivingEventUpload",
    description = "Represents an event that occurred during a trip to be uploaded to the server."
)
public record DrivingEventUpload(
    @Schema(description = "The time the event occurred.")
    @NotNull
    LocalDateTime eventTime,

    @Schema(description = "The location of the event.")
    @NotNull
    @Valid
    Location location,

    @Schema(description = "The type of the event.")
    @NotNull
    EventType eventType,

    @Schema(description = "The severity of the event.")
    @NotNull
    EventType.Severity severity
) { }
