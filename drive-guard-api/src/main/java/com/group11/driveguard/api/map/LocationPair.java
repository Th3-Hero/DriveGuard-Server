package com.group11.driveguard.api.map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(
    name = "LocationPair",
    description = "A pair of locations to be used for distance calculations."
)
public record LocationPair(
    @Valid
    @NotNull(message = "Location one is required")
    Location locationOne,

    @Valid
    @NotNull(message = "Location two is required")
    Location locationTwo
) { }
