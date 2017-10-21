package me.itzg.kidsbank.types;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Document
@Data
public class Kid {
    public static final String FIELD_ACCOUNTS = "accounts";

    @Id
    String username;

    String encPassword;

    List<String> accounts;

    List<String> parents;
}
