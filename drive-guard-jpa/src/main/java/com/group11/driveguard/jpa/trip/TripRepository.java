package com.group11.driveguard.jpa.trip;

import com.group11.driveguard.api.trip.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<TripJpa, Long> {

    List<TripJpa> findAllByDriverIdAndStatus(Long driverId, TripStatus status);

    List<TripJpa> findAllByDriverId(Long driverId);

}
