package com.group11.driveguard.jpa.trip.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrivingEventRepository extends JpaRepository<DrivingEventJpa, Long> {

}
