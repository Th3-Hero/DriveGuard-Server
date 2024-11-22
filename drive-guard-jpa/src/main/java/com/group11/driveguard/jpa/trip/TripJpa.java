package com.group11.driveguard.jpa.trip;

import com.group11.driveguard.api.trip.CompletedTrip;
import com.group11.driveguard.api.trip.Trip;
import com.group11.driveguard.api.trip.TripStatus;
import com.group11.driveguard.jpa.location.LocationJpa;
import com.group11.driveguard.jpa.driver.DriverJpa;
import com.group11.driveguard.jpa.trip.event.DrivingEventJpa;
import com.group11.driveguard.jpa.trip.summary.TripSummaryJpa;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@ToString(exclude = "drivingEvents")
@Table(name = "trip")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TripJpa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trip_id_seq")
    @SequenceGenerator(name = "trip_id_seq", sequenceName = "trip_id_seq", allocationSize = 1)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverJpa driver;

    @Builder.Default
    @NonNull
    @OneToMany(mappedBy = "trip", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DrivingEventJpa> drivingEvents = new ArrayList<>();

    @NonNull
    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @NonNull
    @OneToOne
    @JoinColumn(name = "start_location_id")
    private LocationJpa startLocation;

    @OneToOne
    @JoinColumn(name = "end_location_id")
    private LocationJpa endLocation;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column
    private TripStatus status;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private TripSummaryJpa tripSummaryJpa;

    public static TripJpa create(DriverJpa driver, LocationJpa startLocation) {
        return TripJpa.builder()
                .driver(driver)
                .startLocation(startLocation)
                .startTime(LocalDateTime.now())
                .status(TripStatus.IN_PROGRESS)
                .build();
    }

    public Trip toTripDto() {
        return new Trip(
            id,
            driver.getId(),
            startTime,
            startLocation.toDto(),
            status
        );
    }

    public CompletedTrip toCompletedTripDto() {
        return new CompletedTrip(
            id,
            driver.getId(),
            startTime,
            endTime,
            startLocation.toDto(),
            endLocation.toDto(),
            status,
            tripSummaryJpa.getScore(),
            tripSummaryJpa.getDistance(),
            DrivingEventJpa.toDtoList(drivingEvents)
        );
    }


}
