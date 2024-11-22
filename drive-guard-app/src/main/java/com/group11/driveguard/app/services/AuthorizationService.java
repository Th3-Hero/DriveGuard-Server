package com.group11.driveguard.app.services;

import com.group11.driveguard.api.driver.Driver;
import com.group11.driveguard.api.driver.DriverUpload;
import com.group11.driveguard.api.driver.Session;
import com.group11.driveguard.app.exceptions.CommonErrorMessages;
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
public class AuthorizationService {
    private final DriverRepository driverRepository;
    private final SessionRepository sessionRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

        return driverRepository.save(driverJpa).toDriverDto();
    }

    public Session loginDriver(String username, String password) {
        DriverJpa driverJpa = driverRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException(CommonErrorMessages.MISSING_DRIVER_WITH_USERNAME.formatted(username)));

        validatePassword(driverJpa, password);

        SessionJpa sessionJpa = SessionJpa.create(generateToken(), driverJpa);

        return sessionRepository.save(sessionJpa).toSession();
    }

    public void changePassword(Long driverId, String token, String oldPassword, String newPassword) {
        DriverJpa driverJpa = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException(CommonErrorMessages.MISSING_DRIVER_WITH_ID.formatted(driverId)));

        validateSession(driverId, token);

        // Make sure the old password is correct
        validatePassword(driverJpa, oldPassword);

        String salt = generateSalt();
        String hashedPassword = hashPassword(newPassword, salt);

        driverJpa.setPassword(hashedPassword);
        driverJpa.setSalt(salt);

        driverRepository.save(driverJpa);
    }

    public void removeAllSessions(Long driverId) {
        sessionRepository.deleteAllByIdDriverId(driverId);
    }

    public void logoutDriver(Long driverId, String token) {
        validateSession(driverId, token);

        sessionRepository.deleteById(SessionJpa.createId(driverId, token));
    }


    public void validateSession(Long driverId, String token) {
        if (!sessionRepository.sessionExists(driverId, token)) {
            throw new InvalidCredentialsException(CommonErrorMessages.INVALID_CREDENTIALS);
        }
    }

    public void validatePassword(DriverJpa driverJpa, String password) {
        boolean passwordMatches = passwordEncoder.matches(password + driverJpa.getSalt(), driverJpa.getPassword());
        if (!passwordMatches) {
            throw new InvalidCredentialsException(CommonErrorMessages.INVALID_CREDENTIALS);
        }
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
}
