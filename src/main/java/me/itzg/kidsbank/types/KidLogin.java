package me.itzg.kidsbank.types;

import lombok.Data;
import org.springframework.security.core.CredentialsContainer;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Data
public class KidLogin implements CredentialsContainer {
    String username;
    String password;

    @Override
    public void eraseCredentials() {
        password = "";
    }
}
