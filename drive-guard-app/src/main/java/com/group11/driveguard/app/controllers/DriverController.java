package com.group11.driveguard.app.controllers;

import com.group11.driveguard.api.docs.returns.MinimalProblemDetail;
import com.group11.driveguard.api.docs.returns.MinimalValidationDetail;
import com.group11.driveguard.api.driver.Driver;
import com.group11.driveguard.api.driver.DriverUpload;
import com.group11.driveguard.api.error.ValidationDetail;
import com.group11.driveguard.app.services.DriverManagementService;
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

@Slf4j
@Validated
@RestController
@RequestMapping("/driver")
@Tag(name = "Driver Controller", description = "Handles all operations regarding Drivers(user accounts)")
@RequiredArgsConstructor
public class DriverController {
    private final DriverManagementService driverManagementService;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    @Operation(summary = "Create a new Driver", description = "Note you must login after creating the account to get the token.")
    @ApiResponse(responseCode = "201", description = "Driver created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = { @Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "409", description = "Username already exists", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    public Driver createDriver(@RequestBody @NotNull @Valid DriverUpload driverUpload) {
        return driverManagementService.createDriver(driverUpload);
    }

    @PostMapping("/login")
    @Operation(summary = "Login an existing Driver")
    @ApiResponse(responseCode = "200", description = "Successful login, returns the token. Note: User id and token are required for all operations requiring authentication. DON'T LOSE IT!")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = { @Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver not found", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    public String loginDriver(
        @RequestParam @NotBlank(message = "Username is required") String username,
        @RequestParam @NotBlank(message = "Password is required") String password
    ) {
        return driverManagementService.loginDriver(username, password);
    }

    @PatchMapping("/name/{driverId}")
    @Operation(summary = "Update the name of the Driver")
    @ApiResponse(responseCode = "200", description = "Name updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = { @Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver not found", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    public Driver updateName(
        @PathVariable @NotNull(message = "Driver ID is required") Long driverId,
        @RequestParam @NotBlank(message = "Token is required") String token,
        @RequestParam @NotBlank(message = "First name is required") String firstName,
        @RequestParam @NotBlank(message = "Last name is required") String lastName
    ) {
        return driverManagementService.updateName(driverId, token, firstName, lastName);
    }

    @PatchMapping("/username/{driverId}")
    @Operation(summary = "Update the username of the Driver")
    @ApiResponse(responseCode = "200", description = "Username updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = { @Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver not found", content = {@Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "409", description = "Username already exists", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    public Driver updateUsername(
        @PathVariable @NotNull(message = "Driver ID is required") Long driverId,
        @RequestParam @NotBlank(message = "Token is required") String token,
        @RequestParam @NotBlank(message = "Username is required") String username
    ) {
        return driverManagementService.updateUsername(driverId, token, username);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/password/{driverId}")
    @Operation(summary = "Change the password of the Driver")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = { @Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver not found", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    public void changePassword(
        @PathVariable @NotNull(message = "Driver ID is required") Long driverId,
        @RequestParam @NotBlank(message = "Token is required") String token,
        @RequestParam @NotBlank(message = "Old password is required") String oldPassword,
        @RequestParam @NotBlank(message = "New password is required") String newPassword
    ) {
        driverManagementService.changePassword(driverId, token, oldPassword, newPassword);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{driverId}")
    @Operation(summary = "Delete a Driver account")
    @ApiResponse(responseCode = "204", description = "Driver account deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid fields provided", content = { @Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver not found", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    public void deleteDriver(
        @PathVariable @NotNull(message = "Driver ID is required") Long driverId,
        @RequestParam @NotBlank(message = "Token is required") String token,
        @RequestParam @NotBlank(message = "Password is required") String password
    ) {
        driverManagementService.deleteDriver(driverId, token, password);
    }

    @PostMapping("/recover")
    @Operation(summary = "Recover a deleted Driver account within the recovery period", description = "Note: You must login after recovering the account to get the token.")
    @ApiResponse(responseCode = "200", description = "Driver account recovered successfully")
    @ApiResponse(responseCode = "400", description = "Driver account not deleted", content = { @Content(schema = @Schema(implementation = MinimalValidationDetail.class))})
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    @ApiResponse(responseCode = "404", description = "Driver not found or recovery period expired", content = { @Content(schema = @Schema(implementation = MinimalProblemDetail.class))})
    public Driver recoverDriver(
        @RequestParam @NotBlank(message = "Username is required") String username,
        @RequestParam @NotBlank(message = "Password is required") String password
    ) {
        return driverManagementService.recoverDriver(username, password);
    }

}
