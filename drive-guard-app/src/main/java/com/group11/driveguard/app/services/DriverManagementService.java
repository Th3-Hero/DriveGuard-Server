package com.group11.driveguard.app.services;

import com.group11.driveguard.api.driver.Driver;
import com.group11.driveguard.app.exceptions.CommonErrorMessages;
import com.group11.driveguard.jpa.driver.DriverJpa;
import com.group11.driveguard.jpa.driver.DriverRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class DriverManagementService {

    private final DriverRepository driverRepository;

    private final AuthorizationService authorizationService;
    private final SchedulingService schedulingService;

    public Driver updateName(Long driverId, String token, String firstName, String lastName) {
        DriverJpa driverJpa = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException(CommonErrorMessages.MISSING_DRIVER_WITH_ID.formatted(driverId)));

        authorizationService.validateSession(driverId, token);

        driverJpa.setFirstName(firstName);
        driverJpa.setLastName(lastName);

        return driverRepository.save(driverJpa).toDriverDto();
    }

    public Driver updateUsername(Long driverId, String token, String username) {
        DriverJpa driverJpa = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException(CommonErrorMessages.MISSING_DRIVER_WITH_ID.formatted(driverId)));

        authorizationService.validateSession(driverId, token);

        if (driverRepository.existsByUsername(username)) {
            throw new EntityExistsException("Driver with username already exists");
        }

        driverJpa.setUsername(username);

        return driverRepository.save(driverJpa).toDriverDto();
    }

    public void deleteDriver(Long driverId, String token, String password) {
        DriverJpa driverJpa = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException(CommonErrorMessages.MISSING_DRIVER_WITH_ID.formatted(driverId)));

        authorizationService.validateSession(driverId, token);
        authorizationService.validatePassword(driverJpa, password);

        driverJpa.setDeleted(true);

        // Mark the driver as deleted and delete all auth sessions
        schedulingService.scheduleAccountForDeletion(driverId);
        authorizationService.removeAllSessions(driverId);
        driverRepository.save(driverJpa);
    }

    public Driver recoverDriver(String username, String password) {
        DriverJpa driverJpa = driverRepository.findByUsernameIncludingDeleted(username)
            .orElseThrow(() -> new EntityNotFoundException(CommonErrorMessages.MISSING_DRIVER_WITH_USERNAME.formatted(username)));

        if (!driverJpa.isDeleted()) {
            throw new IllegalStateException("Driver with username is not deleted");
        }

        authorizationService.validatePassword(driverJpa, password);

        schedulingService.removeAccountDeletionTrigger(driverJpa.getId());
        driverJpa.setDeleted(false);
        return driverRepository.save(driverJpa).toDriverDto();
    }


}
