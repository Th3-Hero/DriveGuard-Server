package com.group11.driveguard.api.map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Schema(
    name = "LocationUpload",
    description = "A location to be uploaded to the server."
)
public record Location(
    @NotNull(message = "Latitude is required")
    @DecimalMax(value = "90", message = "Latitude must be less than 90")
    @DecimalMin(value = "-90", message = "Latitude must be greater than -90")
    Double latitude,

    @NotNull(message = "Longitude is required")
    @DecimalMax(value = "180", message = "Longitude must be less than 180")
    @DecimalMin(value = "-180", message = "Longitude must be greater than -180")
    Double longitude
) { }
