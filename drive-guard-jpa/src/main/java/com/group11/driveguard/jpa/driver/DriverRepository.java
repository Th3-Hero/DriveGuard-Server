package com.group11.driveguard.jpa.driver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<DriverJpa, Long> {

    Optional<DriverJpa> findByUsername(String username);

    boolean existsByUsername(String username);
}
