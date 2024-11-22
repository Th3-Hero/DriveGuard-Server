package com.group11.driveguard.app.controllers;

import com.group11.driveguard.api.docs.returns.MinimalProblemDetail;
import com.group11.driveguard.api.docs.returns.MinimalValidationDetail;
import com.group11.driveguard.api.map.Location;
import com.group11.driveguard.api.trip.BasicCompletedTrip;
import com.group11.driveguard.api.trip.CompletedTrip;
import com.group11.driveguard.api.trip.Trip;
import com.group11.driveguard.api.trip.event.DrivingEventUpload;
import com.group11.driveguard.app.services.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/trip")
@Tag(name = "Trip Controller", description = "Handles all operations regarding Trips")
@RequiredArgsConstructor
class TripController {

    private final TripService tripService;

    @GetMapping("/current/{driverId}")
    @Operation(summary = "Get the current trip for a driver")
    @ApiResponse(responseCode = "200", description = "Current trip retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver or trip not found", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "409", description = "Driver is not on a trip", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    Trip getCurrentTrip(
        @PathVariable @NotNull(message = "Driver ID is required") Long driverId,
        @RequestParam @NotBlank(message = "Token is required") String token
    ) {
        return tripService.getCurrentTrip(driverId, token);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{driverId}")
    @Operation(summary = "Start a new trip")
    @ApiResponse(responseCode = "201", description = "Trip started successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver not found", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "409", description = "Driver already on a trip", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    Trip startTrip(
        @PathVariable @NotNull(message = "Driver ID is required") Long driverId,
        @RequestParam @NotBlank(message = "Token is required") String token,
        @RequestBody @NotNull(message = "Start location is required") @Valid Location startLocation
    ) {
        return tripService.startTrip(driverId, token, startLocation);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{driverId}/{tripId}")
    @Operation(summary = "Add a driving event to a trip")
    @ApiResponse(responseCode = "204", description = "Driving event added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver or trip not found", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "409", description = "Trip is already ended", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    void addDrivingEvent(
        @PathVariable @NotNull(message = "Driver ID is required") Long driverId,
        @PathVariable @NotNull(message = "Trip ID is required") Long tripId,
        @RequestParam @NotBlank(message = "Token is required") String token,
        @RequestBody @NotNull(message = "Driving event is required") @Valid DrivingEventUpload drivingEventUpload
    ) {
        tripService.addDrivingEvent(driverId, tripId, token, drivingEventUpload);
    }

    @PatchMapping("/{driverId}/{tripId}")
    @Operation(summary = "End a trip")
    @ApiResponse(responseCode = "200", description = "Trip ended successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver or trip not found", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "409", description = "Trip is already ended", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    CompletedTrip endTrip(
        @PathVariable @NotNull(message = "Driver ID is required") Long driverId,
        @PathVariable @NotNull(message = "Trip ID is required") Long tripId,
        @RequestParam @NotBlank(message = "Token is required") String token,
        @RequestBody @NotNull(message = "End location is required") @Valid Location endLocation
    ) {
        return tripService.endTrip(driverId, tripId, token, endLocation);
    }

    @Operation(summary = "Get a trip summary")
    @ApiResponse(responseCode = "200", description = "Trip summary retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver or trip not found", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "409", description = "Cannot retrieve in-progress trips", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @GetMapping("/{driverId}/{tripId}")
    CompletedTrip getTripSummary(
        @PathVariable @NotNull(message = "Driver ID is required") Long driverId,
        @PathVariable @NotNull(message = "Trip ID is required") Long tripId,
        @RequestParam @NotBlank(message = "Token is required") String token
    ) {
        return tripService.getTripSummary(driverId, tripId, token);
    }

    @Operation(summary = "Get a list of trips")
    @ApiResponse(responseCode = "200", description = "List of trips retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver not found", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @GetMapping("/{driverId}")
    List<BasicCompletedTrip> getListOfTrips(
        @PathVariable @NotNull(message = "Driver ID is required") Long driverId,
        @RequestParam @NotBlank(message = "Token is required") String token
    ) {
        return tripService.getListOfTrips(driverId, token);
    }

    @Operation(summary = "Clear trip history (FOR DEVELOPMENT)", description = "Clears all trip history for a driver (FOR DEVELOPMENT ONLY)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "Trip history cleared successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = {@Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver not found", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @DeleteMapping("/{driverId}")
    void clearTripHistory(
        @PathVariable @NotNull(message = "Driver ID is required") Long driverId,
        @RequestParam @NotBlank(message = "Token is required") String token
    ) {
        tripService.clearTripHistory(driverId, token);
    }

}
