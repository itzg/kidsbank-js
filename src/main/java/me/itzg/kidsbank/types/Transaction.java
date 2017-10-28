package me.itzg.kidsbank.types;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Data
@Document
public class Transaction {
    public static final String FIELD_ACCOUNT_ID = "accountId";
    public static final String FIELD_WHEN = "when";
    public static final String FIELD_AMOUNT = "amount";

    @Null(groups = ForCreate.class)
    @NotEmpty(groups = ForSave.class)
    @Id
    String id;

    @NotEmpty(groups = ForSave.class)
    String accountId;

    String createdBy;
    String modifiedBy;

    @NotNull(groups = {ForCreate.class, ForSave.class})
    Date when;

    @NotEmpty(groups = {ForCreate.class, ForSave.class})
    String description;

    float amount;

    CreationType creationType = CreationType.NORMAL;

    public static enum CreationType {
        NORMAL,
        RESTORE,
        SCHEDULED
    }
}
