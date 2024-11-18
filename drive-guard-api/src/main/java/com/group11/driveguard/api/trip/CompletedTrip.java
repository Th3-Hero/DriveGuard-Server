package com.group11.driveguard.api.trip;

import com.group11.driveguard.api.map.Location;
import com.group11.driveguard.api.trip.event.DrivingEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Schema(
    name = "CompletedTrip",
    description = "Represents a trip that has been completed."
)
public record CompletedTrip(
    @Schema(description = "The trip's id.")
    @NonNull
    Long id,

    @Schema(description = "The driver's id.")
    @NonNull
    Long driverId,

    @Schema(description = "The start time of the trip.")
    @NonNull
    LocalDateTime startTime,

    @Schema(description = "The end time of the trip.")
    @NonNull
    LocalDateTime endTime,

    @Schema(description = "The start location of the trip.")
    @NonNull
    Location startLocation,

    @Schema(description = "The end location of the trip.")
    @NonNull
    Location endLocation,

    @Schema(description = "The status of the trip.")
    @NonNull
    TripStatus status,

    @Schema(description = "The score of the trip.")
    @NonNull
    Integer score,

    @Schema(description = "The distance of the trip.")
    @NonNull
    Double distance,

    @Schema(description = "A list of driving events that occurred during the trip.")
    @NonNull
    List<DrivingEvent> drivingEvents
) { }
