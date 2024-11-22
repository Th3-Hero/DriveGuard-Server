package com.group11.driveguard.api.driver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(
    name = "Session",
    description = "A login session for a driver used for authentication and authorization."
)
public record Session(
    @NonNull
    @Schema(description = "The driver's ID.")
    Long driverId,

    @NonNull
    @Schema(description = "The driver's authentication token.")
    String token
) { }
