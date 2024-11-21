package com.group11.driveguard.jpa.trip.event;


import com.group11.driveguard.api.trip.event.DrivingEvent;
import com.group11.driveguard.api.trip.event.DrivingEventUpload;
import com.group11.driveguard.api.trip.event.EventType;
import com.group11.driveguard.api.trip.event.WeatherType;
import com.group11.driveguard.api.weather.Weather;
import com.group11.driveguard.jpa.location.LocationJpa;
import com.group11.driveguard.jpa.trip.TripJpa;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
    private WeatherType weatherType;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column
    private WeatherType.WeatherSeverity weatherSeverity;

    public static DrivingEventJpa create(DrivingEventUpload drivingEventUpload, Weather weather, LocationJpa location, TripJpa trip) {
        return DrivingEventJpa.builder()
            .trip(trip)
            .eventTime(drivingEventUpload.eventTime())
            .location(location)
            .eventType(drivingEventUpload.eventType())
            .severity(drivingEventUpload.severity())
            .weatherType(weather.getWeatherType())
            .weatherSeverity(weather.getWeatherSeverity())
            .build();
    }

    public DrivingEvent toDto() {
        return new DrivingEvent(
            eventTime,
            location.toDto(),
            eventType,
            severity,
            weatherType
        );
    }

    public static List<DrivingEvent> toDtoList(List<DrivingEventJpa> drivingEventJpaList) {
        return drivingEventJpaList.stream()
            .map(DrivingEventJpa::toDto)
            .toList();
    }
}
