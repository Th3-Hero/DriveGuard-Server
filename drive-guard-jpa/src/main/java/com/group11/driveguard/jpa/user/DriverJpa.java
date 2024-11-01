package com.group11.driveguard.jpa.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "driver")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DriverJpa implements Serializable {

    @Id
    @NonNull
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

    @Range(min = 0, max = 100)
    @NonNull
    @Column
    private Integer overallScore;

    @NonNull
    @Column
    private LocalDateTime accountCreationDate;

    public static DriverJpa create(Long id, String firstName, String lastName, String username, String password, Integer overallScore, LocalDateTime accountCreationDate) {
        return DriverJpa.builder()
            .id(id)
            .firstName(firstName)
            .lastName(lastName)
            .username(username)
            .password(password)
            .overallScore(overallScore)
            .accountCreationDate(accountCreationDate)
            .build();
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
