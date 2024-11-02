package com.group11.driveguard.app.controllers;

import com.group11.driveguard.api.driver.Driver;
import com.group11.driveguard.api.driver.DriverUpload;
import com.group11.driveguard.app.exceptions.InvalidCredentialsException;
import com.group11.driveguard.app.services.DriverManagementService;
import io.swagger.v3.oas.annotations.Operation;
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

@Slf4j
@Validated
@RestController
@RequestMapping("/driver")
@Tag(name = "Driver Controller", description = "Handles all operations regarding Drivers(user accounts)")
@RequiredArgsConstructor
public class DriverController {
    private final DriverManagementService driverManagementService;

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleInvalidClassException(InvalidCredentialsException e) {
        // We could throw a 403 Forbidden here, but 401 Unauthorized hides the existence of the resource
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    @Operation(summary = "Create a new Driver")
    @ApiResponse(responseCode = "201", description = "Driver created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided")
    @ApiResponse(responseCode = "409", description = "Username already exists")
    public Driver createDriver(@RequestBody @NotNull @Valid DriverUpload driverUpload) {
        return driverManagementService.createDriver(driverUpload);
    }

    @PostMapping("/login")
    @Operation(summary = "Login an existing Driver")
    @ApiResponse(responseCode = "200", description = "Successful login, returns the token. Note: User id and token are required for all operations requiring authentication. DON'T LOSE IT!")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public String loginDriver(
        @RequestParam @NotBlank(message = "Username is required") String username,
        @RequestParam @NotBlank(message = "Password is required") String password
    ) {
        return driverManagementService.loginDriver(username, password);
    }

    // Update name
    @PatchMapping("/update/{driverId}/name")
    @Operation(summary = "Update the name of the Driver")
    @ApiResponse(responseCode = "200", description = "Name updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public Driver updateName(
        @PathVariable @NotNull(message = "Driver ID is required") Long driverId,
        @RequestParam @NotBlank(message = "Token is required") String token,
        @RequestParam @NotBlank(message = "First name is required") String firstName,
        @RequestParam @NotBlank(message = "Last name is required") String lastName
    ) {
        return driverManagementService.updateName(driverId, token, firstName, lastName);
    }

    // Update username

    // Change password

    // Delete account
}
