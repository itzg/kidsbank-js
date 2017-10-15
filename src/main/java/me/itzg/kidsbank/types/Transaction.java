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
public class Transaction {
    public static final String FIELD_ACCOUNT_ID = "accountId";
    public static final String FIELD_WHEN = "when";
    public static final String FIELD_AMOUNT = "amount";

    @Id
    String id;

    String accountId;

    String createdBy;

    @NotNull
    Date when;

    @NotEmpty
    String description;

    float amount;
}
