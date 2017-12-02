package me.itzg.kidsbank.types;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Data
@Document
@OneOfIntervalSetValidator.OneOfIntervalSet(groups = ForCreate.class)
public class ScheduledTransaction {
    public static final String FIELD_ACCOUNT_ID = "accountId";

    @Id
    String id;

    @NotEmpty(groups = NeedsAccountId.class)
    String accountId;

    String parentId;

    boolean enabled = true;

    Date nextRun;

    float amount;

    @NotEmpty(groups = ForCreate.class)
    String description;

    @NotNull(groups = ForCreate.class)
    IntervalType intervalType;

    Weekly weekly;

    Monthly monthly;

    public enum IntervalType {
        WEEKLY,
        MONTHLY
    }

    @Data
    public static class Weekly {
        int dayOfWeek;
    }

    @Data
    public static class Monthly {
        int dayOfMonth;
    }

}
