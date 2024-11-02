package com.group11.driveguard.api.driver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

import java.time.LocalDateTime;

@Schema(
    name = "Driver",
    description = "The main entity representing a driver(user) in the system."
)
public record Driver(
    @Schema(description = "The driver's id")
    @NonNull
    Long id,

    @Schema(description = "The driver's first name")
    @NonNull
    String firstName,

    @Schema(description = "The driver's last name")
    @NonNull
    String lastName,

    @Schema(description = "The driver's username")
    @NonNull
    String username,

    @Schema(description = "The driver's overall driving score")
    @NonNull
    Integer overallScore,

    @Schema(description = "The date the account was created")
    @NonNull
    LocalDateTime accountCreationDate
) { }
