package com.group11.driveguard.api.driver;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
    name = "DriverUpload",
    description = "Driver(user) information required for account creation"
)
public record DriverUpload(
    @Schema(description = "First name of the driver")
    @NotBlank(message = "Firstname cannot be blank")
    @Size(min = 2, max = 64, message = "Firstname must be between 2 and 64 characters")
    String firstName,

    @Schema(description = "Last name of the driver")
    @NotBlank(message = "Lastname cannot be blank")
    @Size(min = 2, max = 64, message = "Lastname must be between 2 and 64 characters")
    String lastName,

    @Schema(description = "Username the driver will use to login")
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 6, max = 64, message = "Username must be between 4 and 64 characters")
    String username,

    @Schema(
        description = "Password the driver will use to login",
        example = "Password must contain at least one uppercase letter and one special character"
    )
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[^a-zA-Z0-9].*", message = "Password must contain at least one number.")
    String password
) { }
