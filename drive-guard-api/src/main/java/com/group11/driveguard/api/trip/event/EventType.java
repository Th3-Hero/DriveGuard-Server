package com.group11.driveguard.api.trip.event;

public enum EventType {
    SPEEDING,
    HARD_BRAKING,
    HARD_ACCELERATION,
    HARD_CORNERING;

    public enum Severity {
        LOW,
        MEDIUM,
        HIGH
    }
}
