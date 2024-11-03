package com.group11.driveguard.jpa.driver;

import com.group11.driveguard.api.driver.Driver;
import com.group11.driveguard.jpa.trip.TripJpa;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@ToString(exclude = "trips")
@Table(name = "driver")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DriverJpa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_id_seq")
    @SequenceGenerator(name = "driver_id_seq", sequenceName = "driver_id_seq", allocationSize = 1)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NonNull
    @Column
    private String firstName;

    @NonNull
    @Column
    private String lastName;

    @NonNull
    @Column(unique = true)
    private String username;

    @NonNull
    @Column
    private String password;

    @NonNull
    @Column
    private String salt;

    @Builder.Default
    @Range(min = 0, max = 100)
    @NonNull
    @Column
    private Integer overallScore = 0;

    @Builder.Default
    @NonNull
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<TripJpa> trips = new ArrayList<>();

    @NonNull
    @Column
    private LocalDateTime createdAt;

    @Getter(AccessLevel.NONE)
    @Builder.Default
    @NonNull
    @Column
    private Boolean deleted = false;

    public static DriverJpa create(String firstName, String lastName, String username, String password, String salt) {
        return DriverJpa.builder()
            .firstName(firstName)
            .lastName(lastName)
            .username(username)
            .password(password)
            .salt(salt)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public Driver toDto() {
        return new Driver(id, firstName, lastName, username, overallScore, createdAt);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isDeleted() {
        return this.deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        DriverJpa driverJpa = (DriverJpa) o;
        return id.equals(driverJpa.id) && username.equals(driverJpa.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
