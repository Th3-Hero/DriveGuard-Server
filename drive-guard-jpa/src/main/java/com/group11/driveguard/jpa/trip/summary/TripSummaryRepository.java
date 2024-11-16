package com.group11.driveguard.jpa.trip.summary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripSummaryRepository extends JpaRepository<TripSummaryJpa, Long> {

}
