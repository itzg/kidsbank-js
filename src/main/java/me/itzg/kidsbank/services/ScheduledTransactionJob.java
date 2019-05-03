package me.itzg.kidsbank.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.repositories.ScheduledTransactionRepository;
import me.itzg.kidsbank.types.ScheduledTransaction;
import me.itzg.kidsbank.types.Transaction;
import me.itzg.kidsbank.users.Authorities;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Slf4j
@Component
@Scope("prototype")
public class ScheduledTransactionJob extends QuartzJobBean {

    private final TransactionsService transactionsService;

    private final ScheduledTransactionRepository repository;

    private final Timestamper timestamper;

    private final Counter executionsCounter;

    private ScheduledTransaction scheduledTransaction;

    @Autowired
    public ScheduledTransactionJob(
        TransactionsService transactionsService, ScheduledTransactionRepository repository,
        Timestamper timestamper, MeterRegistry meterRegistry) {
        this.transactionsService = transactionsService;
        this.repository = repository;
        this.timestamper = timestamper;

        executionsCounter = meterRegistry.counter("scheduled_transaction_execution");
    }

    public void setScheduledTransaction(ScheduledTransaction scheduledTransaction) {
        this.scheduledTransaction = scheduledTransaction;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.debug("Executing enabled transaction for {}", scheduledTransaction);
        executionsCounter.increment();

        final PreAuthenticatedAuthenticationToken auth = new PreAuthenticatedAuthenticationToken(
                scheduledTransaction.getParentId(),
                "",
                Collections.singletonList(Authorities.PARENT_AUTHORITY));

        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            Transaction details = new Transaction();
            details.setAccountId(scheduledTransaction.getAccountId());
            details.setCreationType(Transaction.CreationType.SCHEDULED);
            details.setAmount(scheduledTransaction.getAmount());
            details.setDescription(scheduledTransaction.getDescription());
            details.setWhen(timestamper.now());
            transactionsService.createTransaction(details);

            scheduledTransaction.setNextRun(context.getNextFireTime());
            repository.save(scheduledTransaction);
        } finally {
            SecurityContextHolder.getContext().setAuthentication(null);
        }

    }
}
