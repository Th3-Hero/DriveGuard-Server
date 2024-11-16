package com.group11.driveguard.jpa.trip.summary;

import com.group11.driveguard.jpa.trip.TripJpa;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "trip_summary")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TripSummaryJpa implements Serializable {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "trip_id", nullable = false)
    private TripJpa trip;

    @NonNull
    @Column
    private Integer score;

    @NonNull
    @Column
    private Double distance;

    public static TripSummaryJpa create(TripJpa trip, Integer score, Double distance) {
        return TripSummaryJpa.builder()
                .trip(trip)
                .score(score)
                .distance(distance)
                .build();
    }
}
