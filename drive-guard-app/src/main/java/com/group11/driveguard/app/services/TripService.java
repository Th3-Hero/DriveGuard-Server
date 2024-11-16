package com.group11.driveguard.app.services;

import com.group11.driveguard.api.map.Location;
import com.group11.driveguard.api.trip.BasicCompletedTrip;
import com.group11.driveguard.api.trip.CompletedTrip;
import com.group11.driveguard.api.trip.Trip;
import com.group11.driveguard.api.trip.TripStatus;
import com.group11.driveguard.api.trip.event.DrivingEvent;
import com.group11.driveguard.jpa.location.LocationJpa;
import com.group11.driveguard.jpa.driver.DriverJpa;
import com.group11.driveguard.jpa.driver.DriverRepository;
import com.group11.driveguard.jpa.location.LocationRepository;
import com.group11.driveguard.jpa.trip.DrivingEventJpa;
import com.group11.driveguard.jpa.trip.TripJpa;
import com.group11.driveguard.jpa.trip.TripRepository;
import com.group11.driveguard.jpa.trip.summary.TripSummaryJpa;
import com.group11.driveguard.jpa.trip.summary.TripSummaryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final TripSummaryRepository tripSummaryRepository;
    private final LocationRepository locationRepository;

    private final AuthorizationService authorizationService;
    private final DrivingContextService drivingContextService;

    public Trip startTrip(Long driverId, String token, Location startLocation) {

        authorizationService.validateSession(driverId, token);

        DriverJpa driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver %s not found".formatted(driverId)));

        List<TripJpa> trips = tripRepository.findAllByDriverIdAndStatus(driverId, TripStatus.IN_PROGRESS);
        if (!trips.isEmpty()) {
            throw new IllegalStateException("Driver %s is already on trip %s".formatted(driverId, trips.getFirst().getId()));
        }

        TripJpa newTrip = TripJpa.create(driver, locationRepository.save(LocationJpa.fromLocation(startLocation)));

        newTrip = tripRepository.save(newTrip);

        driver.getTrips().add(newTrip);
        driverRepository.save(driver);

        return newTrip.toTripDto();
    }

    public void addDrivingEvent(Long driverId, Long tripId, String token, DrivingEvent drivingEvent) {

    }

    public CompletedTrip endTrip(Long driverId, Long tripId, String token, Location endLocation) {
        authorizationService.validateSession(driverId, token);

        TripJpa tripJpa = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip %s not found".formatted(tripId)));

        if (tripJpa.getStatus() != TripStatus.IN_PROGRESS) {
            throw new IllegalStateException("Trip %s is already ended".formatted(tripId));
        }

        tripJpa.setEndTime(LocalDateTime.now());
        tripJpa.setEndLocation(locationRepository.save(LocationJpa.fromLocation(endLocation)));
        tripJpa.setStatus(TripStatus.COMPLETED);


        double tripDistance = drivingContextService.getDistanceBetweenCoordinates(tripJpa.getStartLocation().toDto(), endLocation);
        int tripScore = calculateTripScore(tripJpa.getDrivingEvents());


        TripSummaryJpa tripSummaryJpa = tripSummaryRepository.save(TripSummaryJpa.create(tripJpa, tripScore, tripDistance));

        tripJpa.setTripSummaryJpa(tripSummaryJpa);

        return tripRepository.save(tripJpa).toCompletedTripDto();
    }

    public CompletedTrip getTripSummary(Long driverId, Long tripId, String token) {
        return null;
    }

    public List<BasicCompletedTrip> getListOfTrips() {
        return null;
    }


    private int calculateTripScore(List<DrivingEventJpa> drivingEvents) {
        return 5;
    }
}
