package com.group11.driveguard.app.jobs;

import com.group11.driveguard.jpa.driver.DriverJpa;
import com.group11.driveguard.jpa.driver.DriverRepository;
import com.group11.driveguard.jpa.driver.session.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

@Slf4j
@RequiredArgsConstructor
public class AccountDeletionJob implements Job {
    public static final JobKey JOB_KEY = JobKey.jobKey("account_deletion");
    public static final String DRIVER_ID = "driver_id";

    private final SessionRepository sessionRepository;
    private final DriverRepository driverRepository;

    @Override
    public void execute(JobExecutionContext executionContext) {
        Long driverId = executionContext.getTrigger().getJobDataMap().getLong(DRIVER_ID);

        DriverJpa driver = driverRepository.findByIdIncludingDeleted(driverId)
            .orElseThrow(() -> new IllegalArgumentException("Driver with id %s not found".formatted(driverId)));

        if (!driver.isDeleted()) {
            throw new IllegalStateException("Driver with id %s is not marked as deleted but was scheduled for deletion. Skipping deletion.".formatted(driverId));
        }

        sessionRepository.deleteAllByIdDriverId(driverId);

        driverRepository.deleteById(driverId);
        log.info("Deleted driver with id: {}", driverId);
    }

}
