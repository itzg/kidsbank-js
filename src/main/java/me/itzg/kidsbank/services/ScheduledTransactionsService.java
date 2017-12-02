package me.itzg.kidsbank.services;

import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.config.ScheduledTransactionProperties;
import me.itzg.kidsbank.repositories.ScheduledTransactionRepository;
import me.itzg.kidsbank.types.ScheduledTransaction;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;

import static org.quartz.CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek;
import static org.quartz.CronScheduleBuilder.monthlyOnDayAndHourAndMinute;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Service
@Slf4j
public class ScheduledTransactionsService {

    private Scheduler scheduler;
    private ScheduledTransactionRepository repository;
    private ScheduledTransactionProperties properties;
    private Random rand;


    @Autowired
    public ScheduledTransactionsService(Scheduler scheduler, ScheduledTransactionRepository repository,
                                        ScheduledTransactionProperties properties,
                                        Random rand) {
        this.scheduler = scheduler;
        this.repository = repository;
        this.properties = properties;
        this.rand = rand;
    }

    @PostConstruct
    public void init() throws SchedulerException {

        final List<ScheduledTransaction> scheduled = repository.findAll();
        for (ScheduledTransaction scheduledTransaction : scheduled) {
            try {
                start(scheduledTransaction);
            } catch (SchedulerException | IllegalArgumentException e) {
                log.warn("Failed to schedule transaction={}", scheduledTransaction, e);
            }
        }

        scheduler.start();
    }

    @PreAuthorize("hasPermission(#scheduledTransaction.accountId, 'Account', 'modifyEntries')")
    public ScheduledTransaction create(ScheduledTransaction scheduledTransaction) throws SchedulerException {

        scheduledTransaction = repository.save(scheduledTransaction);

        try {
            return start(scheduledTransaction);
        } catch (SchedulerException | IllegalArgumentException e) {
            log.warn("Failed to start scheduled={}", scheduledTransaction, e);
            repository.delete(scheduledTransaction);
            throw e;
        }
    }

    @PreAuthorize("hasPermission(#accountId, 'Account', 'readEntries')")
    public List<ScheduledTransaction> getAllForAccount(String accountId) {
        return repository.findByAccountId(accountId);
    }

    @PreAuthorize("hasPermission(#scheduledId, 'ScheduledTransaction', 'delete')")
    public void delete(String scheduledId) throws SchedulerException {

        repository.deleteById(scheduledId);
        scheduler.deleteJob(new JobKey(scheduledId));
    }

    private ScheduledTransaction start(ScheduledTransaction scheduledTransaction) throws SchedulerException {
        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("scheduledTransaction", scheduledTransaction);
        final JobDetail job = JobBuilder.newJob(ScheduledTransactionJob.class)
                .withIdentity(scheduledTransaction.getId())
                .usingJobData(jobDataMap)
                .build();

        final Trigger trigger = buildTrigger(scheduledTransaction);

        scheduledTransaction.setNextRun(scheduler.scheduleJob(job, trigger));
        scheduledTransaction.setEnabled(true);

        log.info("Registered scheduled transaction={}", scheduledTransaction);

        return repository.save(scheduledTransaction);
    }

    private Trigger buildTrigger(ScheduledTransaction scheduledTransaction) {

        return TriggerBuilder.newTrigger()
                .withIdentity(scheduledTransaction.getId())
                .withSchedule(buildSchedule(scheduledTransaction))
                .build();
    }

    private ScheduleBuilder<?> buildSchedule(ScheduledTransaction scheduledTransaction) {
        switch (scheduledTransaction.getIntervalType()) {
            case WEEKLY:
                return atHourAndMinuteOnGivenDaysOfWeek(chooseHour(),
                                                        chooseMinute(),
                                                        scheduledTransaction.getWeekly().getDayOfWeek());

            case MONTHLY:
                return monthlyOnDayAndHourAndMinute(scheduledTransaction.getMonthly().getDayOfMonth(),
                                                    chooseHour(), chooseMinute());
        }
        return null;
    }

    private int chooseMinute() {
        return properties.getMinMinute() +
                rand.nextInt(1 + properties.getMaxMinute() - properties.getMinMinute());
    }

    private int chooseHour() {
        return properties.getMinHour() +
                rand.nextInt(1 + properties.getMaxHour() - properties.getMinHour());
    }
}
