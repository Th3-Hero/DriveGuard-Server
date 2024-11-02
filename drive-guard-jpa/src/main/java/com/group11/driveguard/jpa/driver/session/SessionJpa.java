package com.group11.driveguard.jpa.driver.session;

import com.group11.driveguard.jpa.driver.DriverJpa;
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

    @EmbeddedId
    private SessionId id;

    @ManyToOne
    @MapsId("driverId")
    @JoinColumn(name = "driver_id")
    @NonNull
    private DriverJpa driver;

    @NonNull
    @Column
    private LocalDateTime createdAt;

    public static SessionJpa create(String token, DriverJpa driver) {
        return SessionJpa.builder()
            .id(new SessionId(token, driver.getId()))
            .driver(driver)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public String getToken() {
        return id.getToken();
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
        return id.equals(sessionJpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
