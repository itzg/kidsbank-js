package me.itzg.kidsbank.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Component
@ConfigurationProperties("kidsbank.scheduled")
@Data
public class ScheduledTransactionProperties {
    /**
     * The earliest hour of the day (inclusive) to pick for scheduling transactions.
     */
    int minHour = 0;
    /**
     * The latest hour of the day (inclusive) to pick for scheduling transactions.
     */
    int maxHour = 4;

    int minMinute = 0;
    int maxMinute = 59;
}
