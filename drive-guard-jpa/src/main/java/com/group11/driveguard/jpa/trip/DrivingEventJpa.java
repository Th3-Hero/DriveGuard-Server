package com.group11.driveguard.jpa.trip;


import com.group11.driveguard.api.trip.event.EventType;
import com.group11.driveguard.api.trip.event.Weather;
import com.group11.driveguard.jpa.LocationJpa;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "driving_event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DrivingEventJpa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driving_event_id_seq")
    @SequenceGenerator(name = "driving_event_id_seq", sequenceName = "driving_event_id_seq", allocationSize = 1)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private TripJpa trip;

    @NonNull
    @Column
    private LocalDateTime eventTime;

    @NonNull
    @OneToOne
    @JoinColumn(name = "location_id")
    private LocationJpa location;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column
    private EventType eventType;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column
    private EventType.Severity severity;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column
    private Weather weather;
}
