package com.group11.driveguard.api.trip.event;

import com.group11.driveguard.api.map.Location;

import java.time.LocalDateTime;

public record DrivingEventUpload(
    LocalDateTime eventTime,
    Location location,
    EventType eventType,
    EventType.Severity severity
) { }
