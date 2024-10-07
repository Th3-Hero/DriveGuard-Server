package com.group11.driveguard.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "com.group11.driveguard.*")
@ComponentScan(basePackages = "com.group11.driveguard.*")
@EnableJpaRepositories(basePackages = "com.group11.driveguard.*")
@SpringBootApplication
public class DriveGuardApp {

    public static void main(String[] args) {
        SpringApplication.run(DriveGuardApp.class, args);
    }
}
