package me.itzg.kidsbank.types;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.CredentialsContainer;

import javax.validation.constraints.NotNull;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Data
public class KidLogin implements CredentialsContainer {
    @NotEmpty
    String username;
    @NotNull
    String password;

    @Override
    public void eraseCredentials() {
        password = "";
    }
}
