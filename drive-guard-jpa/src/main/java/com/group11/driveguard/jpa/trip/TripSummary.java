package com.group11.driveguard.jpa.trip;

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
public class TripSummary implements Serializable {

    @Id
    @OneToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private TripJpa trip;

    @NonNull
    @Column
    private Integer score;

    @NonNull
    @Column
    private Double distance;
}
