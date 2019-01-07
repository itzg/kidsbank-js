package me.itzg.kidsbank.web;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import me.itzg.kidsbank.repositories.ParentRepository;
import me.itzg.kidsbank.services.ScheduledTransactionsService;
import me.itzg.kidsbank.types.ScheduledTransaction;
import me.itzg.kidsbank.users.ParentOAuth2DetailsLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Geoff Bourne
 * @since Dec 2017
 */
@RunWith(SpringRunner.class)
public class ParentScheduledApiTest {

    @Configuration
    @Import({ParentScheduledApi.class, ParentOAuth2DetailsLoader.class})
    static class Config {
        @MockBean
        public ScheduledTransactionsService scheduledTransactionsService;

        @MockBean
        public MongoTemplate mongoTemplate;

        @MockBean
        public ParentRepository parentRepository;

        @Bean
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @Autowired
    private ParentScheduledApi parentScheduledApi;

    @Test(expected = IllegalArgumentException.class)
    public void createScheduledTransaction_nothingSet() throws SchedulerException {
        ScheduledTransaction scheduledTransaction = new ScheduledTransaction();
        parentScheduledApi.createScheduledTransaction(null, "ac-1", scheduledTransaction);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createScheduledTransaction_justAmount() throws SchedulerException {
        ScheduledTransaction scheduledTransaction = new ScheduledTransaction();
        scheduledTransaction.setAmount(4.25f);
        parentScheduledApi.createScheduledTransaction(null, "ac-1", scheduledTransaction);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createScheduledTransaction_invalidDayOfMonth() throws SchedulerException {
        ScheduledTransaction scheduledTransaction = new ScheduledTransaction();
        scheduledTransaction.setAmount(4.25f);
        scheduledTransaction.setIntervalType(ScheduledTransaction.IntervalType.MONTHLY);
        scheduledTransaction.setMonthly(new ScheduledTransaction.Monthly());
        scheduledTransaction.getMonthly().setDayOfMonth(50);
        parentScheduledApi.createScheduledTransaction(null, "ac-1", scheduledTransaction);
    }

    @Test
    public void createScheduledTransaction_validDayOfMonth() throws SchedulerException {
        ScheduledTransaction scheduledTransaction = new ScheduledTransaction();
        scheduledTransaction.setAmount(4.25f);
        scheduledTransaction.setIntervalType(ScheduledTransaction.IntervalType.MONTHLY);
        scheduledTransaction.setMonthly(new ScheduledTransaction.Monthly());
        scheduledTransaction.getMonthly().setDayOfMonth(12);
        parentScheduledApi.createScheduledTransaction(new UsernamePasswordAuthenticationToken("", ""),
                                                      "ac-1",
                                                      scheduledTransaction);
    }
}