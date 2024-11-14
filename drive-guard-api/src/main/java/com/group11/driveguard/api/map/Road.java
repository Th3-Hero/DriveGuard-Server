package com.group11.driveguard.api.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(
    name = "Road",
    description = "Represents a road in the map."
)
public record Road(
    @NonNull
    @Schema(description = "The location of the road.")
    Location location,

    @NonNull
    @Schema(description = "The type of the road.")
    String type,

    @NonNull
    @Schema(description = "The name of the road.")
    String name,

    @NonNull
    @Schema(description = "The speed limit of the road.")
    int speedLimit
) { }