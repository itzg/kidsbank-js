package me.itzg.kidsbank.types;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Data
public class AccountCreation {
    @NotEmpty
    String name;
}
