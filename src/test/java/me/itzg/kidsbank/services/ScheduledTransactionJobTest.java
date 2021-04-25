package me.itzg.kidsbank.services;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Map;
import me.itzg.kidsbank.errors.NotFoundException;
import me.itzg.kidsbank.repositories.ParentRepository;
import me.itzg.kidsbank.repositories.TransactionRepository;
import me.itzg.kidsbank.types.Account;
import me.itzg.kidsbank.types.AccountCreation;
import me.itzg.kidsbank.types.Parent;
import me.itzg.kidsbank.types.ScheduledTransaction;
import me.itzg.kidsbank.types.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduledTransactionJobTest {

    @Autowired
    ScheduledTransactionJob scheduledTransactionJob;

    @Autowired
    ParentRepository parentRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    AccountsService accountsService;

    @Autowired
    TransactionRepository transactionRepository;

    @Mock
    JobExecutionContext jobExecutionContext;

    @Mock
    Scheduler scheduler;

    @Test
    public void testJobRun() throws SchedulerException, NotFoundException {

        final Parent parent = parentRepository.save(new Parent());

        final Account account = accountsService.createAccount(parent.getId(), new AccountCreation().setName("Child"));

        when(jobExecutionContext.getScheduler()).thenReturn(scheduler);
        final SchedulerContext context = new SchedulerContext(Map.of());
        when(scheduler.getContext()).thenReturn(context);

        final ScheduledTransaction scheduledTransaction = new ScheduledTransaction()
            .setParentId(parent.getId())
            .setAccountId(account.getId())
            .setAmount(1.23f)
            .setDescription("Allowance");
        final JobDataMap jobData = new JobDataMap(Map.of("scheduledTransaction", scheduledTransaction));
        when(jobExecutionContext.getMergedJobDataMap()).thenReturn(jobData);

        when(jobExecutionContext.getNextFireTime()).thenReturn(new Date(1619316857000L));

        scheduledTransactionJob.execute(jobExecutionContext);

        final Page<Transaction> results = transactionRepository.findByAccountId(account.getId(), Pageable.unpaged());
        assertThat(results.getContent(), hasSize(1));
        assertThat(results.getContent().get(0).getAmount(), is(1.23f));
    }
}