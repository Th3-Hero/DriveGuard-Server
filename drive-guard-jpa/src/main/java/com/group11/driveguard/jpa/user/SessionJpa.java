package com.group11.driveguard.jpa.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "session")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionJpa implements Serializable {

    @Id
    @NonNull
    @Setter(AccessLevel.NONE)
    private String token;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    @NonNull
    private DriverJpa driver;

    @NonNull
    @Column
    private LocalDateTime creationDate;

    public static SessionJpa create(String token, DriverJpa driver, LocalDateTime creationDate) {
        return SessionJpa.builder()
            .token(token)
            .driver(driver)
            .creationDate(creationDate)
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

        SessionJpa sessionJpa = (SessionJpa) o;
        return token.equals(sessionJpa.token) && driver.equals(sessionJpa.driver);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(token);
    }

}
