package com.group11.driveguard.api.trip;

import java.time.Duration;

public record BasicCompletedTrip (
    Long id,
    Long driverId,
    Double distanceKM,
    Duration duration
) { }
