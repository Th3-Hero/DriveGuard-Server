package com.group11.driveguard.app.controllers;

import com.group11.driveguard.api.docs.returns.MinimalValidationDetail;
import com.group11.driveguard.api.map.Address;
import com.group11.driveguard.api.map.Location;
import com.group11.driveguard.api.map.LocationPair;
import com.group11.driveguard.api.map.Road;
import com.group11.driveguard.api.weather.Weather;
import com.group11.driveguard.app.services.DrivingContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/driving-context")
@Tag(name = "Driving Context Controller", description = "Handles all operations regarding the driving context")
@RequiredArgsConstructor
public class DrivingContextController {

    private final DrivingContextService drivingContextService;

    @PostMapping("/address")
    @Operation(summary = "Get address from coordinates")
    @ApiResponse(responseCode = "200", description = "Address found")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    Address getAddressFromCoordinates(
        @RequestBody @NotNull(message = "Location is required") @Valid Location location
    ) {
        return drivingContextService.getAddressFromCoordinates(location);
    }

    @PostMapping("/road")
    @Operation(summary = "Get road from coordinates")
    @ApiResponse(responseCode = "200", description = "Road found")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    Road getRoadFromCoordinates(
        @RequestBody @NotNull(message = "Location is required") @Valid Location location
    ) {
        return drivingContextService.getRoadFromCoordinates(location);
    }

    @PostMapping("/distance")
    @Operation(summary = "Get estimated driving distance between coordinates")
    @ApiResponse(responseCode = "200", description = "Distance found")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    double estimatedDrivingDistanceBetweenCoordinatesKM(
        @RequestBody @NotNull(message = "Location pair is required") @Valid LocationPair locationPair
    ) {
        return drivingContextService.getDistanceBetweenCoordinates(locationPair);
    }

    @PostMapping("/weather")
    @Operation(summary = "Get weather from coordinates")
    @ApiResponse(responseCode = "200", description = "Weather found")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    Weather getWeatherFromCoordinates(
        @RequestBody @NotNull(message = "Location is required") @Valid Location location
    ) {
        return drivingContextService.getWeatherFromCoordinates(location);
    }
}
