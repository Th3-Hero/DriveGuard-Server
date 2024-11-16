package com.group11.driveguard.api.trip;

import com.group11.driveguard.api.map.Location;
import com.group11.driveguard.api.trip.event.DrivingEvent;

import java.time.LocalDateTime;
import java.util.List;

public record CompletedTrip(
    Long id,
    Long driverId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Location startLocation,
    Location endLocation,
    TripStatus status,
    Integer score,
    Double distance,
    List<DrivingEvent> drivingEvents
) { }
