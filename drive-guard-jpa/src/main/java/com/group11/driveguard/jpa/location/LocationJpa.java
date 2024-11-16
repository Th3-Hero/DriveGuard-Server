package com.group11.driveguard.jpa.location;

import com.group11.driveguard.api.map.Location;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "location")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LocationJpa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_id_seq")
    @SequenceGenerator(name = "location_id_seq", sequenceName = "location_id_seq", allocationSize = 1)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NonNull
    @Column
    private Double latitude;

    @NonNull
    @Column
    private Double longitude;

    public static LocationJpa fromLocation(Location location) {
        return LocationJpa.builder()
                .latitude(location.latitude())
                .longitude(location.longitude())
                .build();
    }

    public Location toDto() {
        return new Location(latitude, longitude);
    }
}
