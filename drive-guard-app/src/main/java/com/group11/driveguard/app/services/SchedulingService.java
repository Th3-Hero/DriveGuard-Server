package com.group11.driveguard.app.services;

import com.group11.driveguard.app.jobs.AccountDeletionJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.SchedulingException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulingService {
    private final Scheduler scheduler;

    @Value("${config.account-recovery-time}")
    private Integer accountRecoveryTime;

    private static final ZoneId ZONE_ID = ZoneId.of("America/New_York");

    /**
     * Schedules an account for deletion after the recovery period.
     *
     * @param driverId The id of the driver account to schedule for deletion
     * @throws SchedulingException If the account cannot be scheduled for deletion
     */
    public void scheduleAccountForDeletion(Long driverId) {
        try {
            createJobIfNone(AccountDeletionJob.JOB_KEY, AccountDeletionJob.class, "Deletes a driver account after the recovery period");

            TriggerKey key = TriggerKey.triggerKey(driverId.toString());
            Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(key)
                .forJob(AccountDeletionJob.JOB_KEY)
                .startAt(toDate(LocalDateTime.now().plusDays(accountRecoveryTime)))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                    .withMisfireHandlingInstructionFireNow()
                    .withRepeatCount(0)
                )
                .build();

            scheduler.scheduleJob(trigger);
            log.info("Scheduled account for deletion: {}", driverId);
        } catch (SchedulerException e) {
            throw new SchedulingException("Failed to schedule account with id %s for deletion".formatted(driverId), e);
        }
    }

    /**
     * Removes the account deletion trigger for a driver.
     *
     * @param driverId The id of the driver account to remove the trigger for
     * @throws SchedulingException If the trigger cannot be removed
     */
    public void removeAccountDeletionTrigger(Long driverId) {
        try {
            scheduler.unscheduleJob(TriggerKey.triggerKey(driverId.toString()));
            log.info("Removed account deletion trigger for driver: {}", driverId);
        } catch (SchedulerException e) {
            throw new SchedulingException("Failed to remove account deletion trigger for driver: %s".formatted(driverId), e);
        }
    }


    /**
     * Creates a quartz job if one does not already exist.
     *
     * @param jobKey The key of the job
     * @param jobClass The class of the job
     * @param description The description of the job
     * @throws SchedulerException If the job cannot be created
     */
    private <T extends Job> void createJobIfNone(JobKey jobKey, Class<T> jobClass, String description) throws SchedulerException {
        if (!scheduler.checkExists(jobKey)) {
            JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .storeDurably()
                .withDescription(description)
                .build();
            scheduler.addJob(job, true);
            log.info("Added job: {}", jobKey.getName());
        }
    }

    private Date toDate(LocalDateTime date) {
        return Date.from(date.atZone(ZONE_ID).toInstant());
    }
}
