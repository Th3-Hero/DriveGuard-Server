package com.group11.driveguard.jpa.driver;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<DriverJpa, Long> {

    @Query("select d from DriverJpa d where d.id = :id and d.deleted = false")
    @NonNull Optional<DriverJpa> findById(@NonNull Long id);

    @Query("select d from DriverJpa d where d.id = :id")
    Optional<DriverJpa> findByIdIncludingDeleted(@NonNull Long id);

    @Query("select d from DriverJpa d where d.username = :username and d.deleted = false")
    Optional<DriverJpa> findByUsername(String username);

    @Query("select d from DriverJpa d where d.username = :username")
    Optional<DriverJpa> findByUsernameIncludingDeleted(String username);

    boolean existsByUsername(String username);
}
