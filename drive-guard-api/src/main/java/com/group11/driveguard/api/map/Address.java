package com.group11.driveguard.api.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(
    name = "Address",
    description = "Represents an address in the map."
)
public record Address(
    @NonNull
    @Schema(description = "The location of the address.")
    Location location,

    @NonNull
    @Schema(description = "The street of the address.")
    String street,

    @NonNull
    @Schema(description = "The city of the address.")
    String city,

    @NonNull
    @Schema(description = "The state of the address.")
    String state,

    @NonNull
    @Schema(description = "The postal code of the address.")
    String postalCode,

    @NonNull
    @Schema(description = "The country of the address.")
    String country
) { }
