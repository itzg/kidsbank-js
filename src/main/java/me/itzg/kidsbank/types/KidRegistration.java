package me.itzg.kidsbank.types;

import lombok.Data;
import org.springframework.security.core.CredentialsContainer;

import javax.validation.constraints.NotEmpty;

/**
 * Represents the fields needed for a kid to register.
 *
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Data
public class KidRegistration implements CredentialsContainer {
    @NotEmpty
    String username;
    @NotEmpty
    String password;
    @NotEmpty
    String kidlinkCode;

    @Override
    public void eraseCredentials() {
        password = "";
    }
}
