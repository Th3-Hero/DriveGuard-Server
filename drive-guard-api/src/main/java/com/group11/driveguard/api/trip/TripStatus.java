package com.group11.driveguard.api.trip;

public enum TripStatus {
    IN_PROGRESS,
    PAUSED,
    // NOTE: Trips cannot be cancelled by the user.
    // They can only be cancelled by the system if the trip isn't long enough or doesn't travel far enough.
    // This stops padding trip scores.
    CANCELLED,
    COMPLETED
}
