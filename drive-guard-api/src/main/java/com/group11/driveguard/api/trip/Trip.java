package com.group11.driveguard.api.trip;

import com.group11.driveguard.api.map.Location;

import java.time.LocalDateTime;

public record Trip(
    Long id,
    Long driverId,
    LocalDateTime startTime,
    Location startLocation,
    TripStatus status
) { }
