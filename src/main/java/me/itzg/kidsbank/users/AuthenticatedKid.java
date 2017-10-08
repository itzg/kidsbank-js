package me.itzg.kidsbank.users;

import lombok.Data;
import me.itzg.kidsbank.types.Kid;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.CredentialsContainer;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Data
public class AuthenticatedKid implements AuthenticatedPrincipal, CredentialsContainer {

    final Kid kid;

    @Override
    public String getName() {
        return kid.getUsername();
    }

    @Override
    public void eraseCredentials() {
        kid.setEncPassword("");
    }
}
