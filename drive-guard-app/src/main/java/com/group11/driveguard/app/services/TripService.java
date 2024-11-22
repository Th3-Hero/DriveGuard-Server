package com.group11.driveguard.app.services;

import com.group11.driveguard.api.map.Location;
import com.group11.driveguard.api.trip.BasicCompletedTrip;
import com.group11.driveguard.api.trip.CompletedTrip;
import com.group11.driveguard.api.trip.Trip;
import com.group11.driveguard.api.trip.TripStatus;
import com.group11.driveguard.api.trip.event.DrivingEventUpload;
import com.group11.driveguard.api.weather.Weather;
import com.group11.driveguard.jpa.driver.DriverJpa;
import com.group11.driveguard.jpa.driver.DriverRepository;
import com.group11.driveguard.jpa.location.LocationJpa;
import com.group11.driveguard.jpa.location.LocationRepository;
import com.group11.driveguard.jpa.trip.TripJpa;
import com.group11.driveguard.jpa.trip.TripRepository;
import com.group11.driveguard.jpa.trip.event.DrivingEventJpa;
import com.group11.driveguard.jpa.trip.event.DrivingEventRepository;
import com.group11.driveguard.jpa.trip.summary.TripSummaryJpa;
import com.group11.driveguard.jpa.trip.summary.TripSummaryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    private final DrivingEventRepository drivingEventRepository;

    private final AuthorizationService authorizationService;
    private final DrivingContextService drivingContextService;

    public static final String TRIP_NOT_FOUND = "Trip %s not found";

    public Trip getCurrentTrip(Long driverId, String token) {
        authorizationService.validateSession(driverId, token);

        List<TripJpa> trips = tripRepository.findAllByDriverIdAndStatus(driverId, TripStatus.IN_PROGRESS);
        if (trips.isEmpty()) {
            throw new EntityNotFoundException("Driver %s is not on a trip".formatted(driverId));
        }

        return trips.getFirst().toTripDto();
    }

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

    public void addDrivingEvent(Long driverId, Long tripId, String token, DrivingEventUpload drivingEventUpload) {
        authorizationService.validateSession(driverId, token);

        TripJpa tripJpa = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException(TRIP_NOT_FOUND.formatted(tripId)));
        if (!tripJpa.getStatus().equals(TripStatus.IN_PROGRESS)) {
            throw new IllegalStateException("Trip %s is already complete".formatted(tripId));
        }

        Weather weather = drivingContextService.getWeatherFromCoordinates(drivingEventUpload.location());

        LocationJpa eventLocation = locationRepository.save(LocationJpa.fromLocation(drivingEventUpload.location()));

        DrivingEventJpa drivingEventJpa = DrivingEventJpa.create(drivingEventUpload, weather, eventLocation, tripJpa);

        drivingEventJpa = drivingEventRepository.save(drivingEventJpa);

        tripJpa.getDrivingEvents().add(drivingEventJpa);
        tripRepository.save(tripJpa);
    }

    public CompletedTrip endTrip(Long driverId, Long tripId, String token, Location endLocation) {
        authorizationService.validateSession(driverId, token);

        TripJpa tripJpa = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException(TRIP_NOT_FOUND.formatted(tripId)));

        if (!tripJpa.getStatus().equals(TripStatus.IN_PROGRESS)) {
            throw new IllegalStateException("Trip %s is already complete".formatted(tripId));
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
        authorizationService.validateSession(driverId, token);

        TripJpa tripJpa = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException(TRIP_NOT_FOUND.formatted(tripId)));
        if (!tripJpa.getStatus().equals(TripStatus.COMPLETED)) {
            throw new IllegalStateException("Trip %s is not completed".formatted(tripId));
        }

        return tripJpa.toCompletedTripDto();
    }

    public List<BasicCompletedTrip> getListOfTrips(Long driverId, String token) {
        authorizationService.validateSession(driverId, token);

        List<TripJpa> trips = tripRepository.findAllByDriverIdAndStatus(driverId, TripStatus.COMPLETED);

        return trips.stream()
            .map(trip ->
                new BasicCompletedTrip(
                    trip.getId(),
                    trip.getDriver().getId(),
                    trip.getTripSummaryJpa().getScore(),
                    trip.getTripSummaryJpa().getDistance(),
                    Duration.between(trip.getStartTime(), trip.getEndTime())
                )
            )
            .toList();
    }

    public void clearTripHistory(Long driverId, String token) {
        authorizationService.validateSession(driverId, token);

        DriverJpa driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException("Driver %s not found".formatted(driverId)));

        List<TripJpa> trips = tripRepository.findAllByDriverId(driverId);

        driver.getTrips().clear();
        driver.setOverallScore(0);

        driverRepository.save(driver);
        tripRepository.deleteAll(trips);
    }


    private int calculateTripScore(List<DrivingEventJpa> drivingEvents) {
        // TODO: For Brooke to implement
        return 5;
    }
}
