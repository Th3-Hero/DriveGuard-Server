package com.group11.driveguard.app.exceptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.NONE)
public class CommonErrorMessages {
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String MISSING_DRIVER_WITH_ID = "Unknown driver with ID %s";
    public static final String MISSING_DRIVER_WITH_USERNAME = "Unknown driver with username %s";
}
