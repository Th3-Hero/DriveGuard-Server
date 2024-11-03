package com.group11.driveguard.jpa.trip;

import com.group11.driveguard.api.trip.TripStatus;
import com.group11.driveguard.jpa.LocationJpa;
import com.group11.driveguard.jpa.driver.DriverJpa;
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
    @OneToMany(mappedBy = "trip", fetch = FetchType.LAZY)
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

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL, optional = false)
    private TripSummary tripSummary;

}
