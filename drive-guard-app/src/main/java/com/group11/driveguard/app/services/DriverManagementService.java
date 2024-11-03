package com.group11.driveguard.app.services;

import com.group11.driveguard.api.driver.Driver;
import com.group11.driveguard.api.driver.DriverUpload;
import com.group11.driveguard.app.exceptions.InvalidCredentialsException;
import com.group11.driveguard.jpa.driver.DriverJpa;
import com.group11.driveguard.jpa.driver.DriverRepository;
import com.group11.driveguard.jpa.driver.session.SessionJpa;
import com.group11.driveguard.jpa.driver.session.SessionRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;


@Service
@Transactional
@RequiredArgsConstructor
public class DriverManagementService {

    private final DriverRepository driverRepository;
    private final SessionRepository sessionRepository;

    private final SchedulingService schedulingService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String INVALID_CREDENTIALS = "Invalid credentials";
    private static final String MISSING_DRIVER_WITH_ID = "Unknown driver with ID %s";
    private static final String MISSING_DRIVER_WITH_USERNAME = "Unknown driver with username %s";

    public Driver createDriver(DriverUpload driverUpload) {
        if (driverRepository.existsByUsername(driverUpload.username())) {
            throw new EntityExistsException("Driver with username already exists");
        }

        String salt = generateSalt();
        String hashedPassword = hashPassword(driverUpload.password(), salt);

        DriverJpa driverJpa = DriverJpa.create(
            driverUpload.firstName(),
            driverUpload.lastName(),
            driverUpload.username(),
            hashedPassword,
            salt
        );

        return driverRepository.save(driverJpa).toDto();
    }

    public String loginDriver(String username, String password) {
        DriverJpa driverJpa = driverRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException(MISSING_DRIVER_WITH_USERNAME.formatted(username)));

        if (!verifyPassword(password, driverJpa)) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }

        SessionJpa sessionJpa = SessionJpa.create(generateToken(), driverJpa);

        return sessionRepository.save(sessionJpa).getToken();
    }

    public Driver updateName(Long driverId, String token, String firstName, String lastName) {
        DriverJpa driverJpa = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException(MISSING_DRIVER_WITH_ID.formatted(driverId)));

        verifyAuthentication(driverId, token);

        driverJpa.setFirstName(firstName);
        driverJpa.setLastName(lastName);

        return driverRepository.save(driverJpa).toDto();
    }

    public Driver updateUsername(Long driverId, String token, String username) {
        DriverJpa driverJpa = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException(MISSING_DRIVER_WITH_ID.formatted(driverId)));

        verifyAuthentication(driverId, token);

        if (driverRepository.existsByUsername(username)) {
            throw new EntityExistsException("Driver with username already exists");
        }

        driverJpa.setUsername(username);

        return driverRepository.save(driverJpa).toDto();
    }

    public void changePassword(Long driverId, String token, String oldPassword, String newPassword) {
        DriverJpa driverJpa = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException(MISSING_DRIVER_WITH_ID.formatted(driverId)));

        verifyAuthentication(driverId, token);

        // Make sure the old password is correct
        if (!verifyPassword(oldPassword, driverJpa)) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }

        String salt = generateSalt();
        String hashedPassword = hashPassword(newPassword, salt);

        driverJpa.setPassword(hashedPassword);
        driverJpa.setSalt(salt);

        driverRepository.save(driverJpa);
    }

    public void deleteDriver(Long driverId, String token, String password) {
        DriverJpa driverJpa = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException(MISSING_DRIVER_WITH_ID.formatted(driverId)));

        verifyAuthentication(driverId, token);

        if (!verifyPassword(password, driverJpa)) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }

        driverJpa.setDeleted(true);

        // Mark the driver as deleted and delete all auth sessions
        schedulingService.scheduleAccountForDeletion(driverId);
        sessionRepository.deleteAllByIdDriverId(driverId);
        driverRepository.save(driverJpa);
    }

    public Driver recoverDriver(String username, String password) {
        DriverJpa driverJpa = driverRepository.findByUsernameIncludingDeleted(username)
            .orElseThrow(() -> new EntityNotFoundException(MISSING_DRIVER_WITH_USERNAME.formatted(username)));

        if (!driverJpa.isDeleted()) {
            throw new IllegalStateException("Driver with username is not deleted");
        }

        if (!verifyPassword(password, driverJpa)) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }

        schedulingService.removeAccountDeletionTrigger(driverJpa.getId());
        driverJpa.setDeleted(false);
        return driverRepository.save(driverJpa).toDto();
    }

    private String generateSalt() {
        final SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String generateToken() {
        final SecureRandom random = new SecureRandom();
        byte[] token = new byte[64];
        random.nextBytes(token);
        return Base64.getEncoder().encodeToString(token);
    }

    private String hashPassword(String password, String salt) {
        return passwordEncoder.encode(password + salt);
    }

    private boolean verifyPassword(String password, DriverJpa driverJpa) {
        return passwordEncoder.matches(password + driverJpa.getSalt(), driverJpa.getPassword());
    }

    private void verifyAuthentication(Long driverId, String token) {
        if (!sessionRepository.sessionExists(driverId, token)) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }
    }

}
