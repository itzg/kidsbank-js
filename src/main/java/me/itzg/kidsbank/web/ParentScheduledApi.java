package me.itzg.kidsbank.web;

import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.services.ScheduledTransactionsService;
import me.itzg.kidsbank.types.ForCreate;
import me.itzg.kidsbank.types.ScheduledTransaction;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@RestController
@RequestMapping(Paths.API + Paths.PARENT)
@Slf4j
public class ParentScheduledApi {

    private ScheduledTransactionsService scheduledTransactionsService;

    @Autowired
    public ParentScheduledApi(ScheduledTransactionsService scheduledTransactionsService) {
        this.scheduledTransactionsService = scheduledTransactionsService;
    }

    @PostMapping("accounts/{accountId}/scheduled")
    public ScheduledTransaction createScheduledTransaction(Principal principal,
                                                           @PathVariable String accountId,
                                                           @RequestBody @Validated(ForCreate.class) ScheduledTransaction scheduledTransaction) throws SchedulerException {
        validateDetails(scheduledTransaction);
        scheduledTransaction.setParentId(principal.getName());
        scheduledTransaction.setAccountId(accountId);
        return scheduledTransactionsService.create(scheduledTransaction);
    }

    private void validateDetails(ScheduledTransaction scheduledTransaction) {
        if (scheduledTransaction.getAmount() == 0.0f) {
            throw new IllegalArgumentException("Amount needs to be non-zero");
        }

        if (scheduledTransaction.getIntervalType() == null) {
            throw new IllegalArgumentException("Interval type needs to be set");
        }

        switch (scheduledTransaction.getIntervalType()) {
            case MONTHLY:
                final int dayOfMonth = scheduledTransaction.getMonthly().getDayOfMonth();
                if (dayOfMonth < 1 || dayOfMonth > 31) {
                    throw new IllegalArgumentException("Invalid day of month");
                }
                break;

            case WEEKLY:
                final int dayOfWeek = scheduledTransaction.getWeekly().getDayOfWeek();
                if (dayOfWeek < 1 || dayOfWeek > 7) {
                    throw new IllegalArgumentException("Invalid day of week");
                }
                break;
        }
    }

    @GetMapping("accounts/{accountId}/scheduled")
    public List<ScheduledTransaction> getScheduledTransactions(@PathVariable String accountId) {
        return scheduledTransactionsService.getAllForAccount(accountId);
    }

    @DeleteMapping("scheduled/{scheduledId}")
    public void deleteScheduleTransaction(@PathVariable String scheduledId) throws SchedulerException {
        scheduledTransactionsService.delete(scheduledId);
    }

}
