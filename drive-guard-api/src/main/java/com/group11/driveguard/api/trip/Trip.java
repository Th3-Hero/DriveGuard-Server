package com.group11.driveguard.api.trip;

import com.group11.driveguard.api.map.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

import java.time.LocalDateTime;

@Schema(
    name = "Trip",
    description = "Represents a trip."
)
public record Trip(
    @Schema(description = "The trip's id.")
    @NonNull
    Long id,

    @Schema(description = "The driver's id.")
    @NonNull
    Long driverId,

    @Schema(description = "The start time of the trip.")
    @NonNull
    LocalDateTime startTime,

    @Schema(description = "The start location of the trip.")
    @NonNull
    Location startLocation,

    @Schema(description = "The status of the trip.")
    @NonNull
    TripStatus status
) { }
