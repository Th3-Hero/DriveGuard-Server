package com.group11.driveguard.jpa.driver.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<SessionJpa, SessionId> {

    boolean existsByIdDriverIdAndIdToken(Long driverId, String token);

    void deleteAllByIdDriverId(Long driverId);

    default boolean sessionExists(Long driverId, String token) {
        return existsByIdDriverIdAndIdToken(driverId, token);
    }
}
