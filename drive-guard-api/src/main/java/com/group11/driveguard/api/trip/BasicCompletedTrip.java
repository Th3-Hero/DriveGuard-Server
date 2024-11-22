package com.group11.driveguard.api.trip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

import java.time.Duration;

@Schema(
    name = "BasicCompletedTrip",
    description = "A simplified representation of a completed trip for listing trips"
)
public record BasicCompletedTrip (
    @NonNull
    @Schema(description = "The id of the trip.")
    Long id,

    @NonNull
    @Schema(description = "The id of the driver.")
    Long driverId,

    @NonNull
    @Schema(description = "The score of the trip.")
    Integer score,

    @NonNull
    @Schema(description = "The distance of the trip in kilometers.")
    Double distanceKM,

    @NonNull
    @Schema(description = "The duration of the trip.")
    Duration duration
) { }
