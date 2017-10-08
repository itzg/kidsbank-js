package me.itzg.kidsbank.users;

import me.itzg.kidsbank.types.KidLogin;
import me.itzg.kidsbank.types.KidRegistration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public class KidAuthenticationToken extends AbstractAuthenticationToken {
    private KidRegistration kidRegistration;
    private KidAuthenticationToken kidAuth;
    private AuthenticatedKid authenticatedKid;
    private KidLogin kidLogin;

    public KidAuthenticationToken(KidRegistration kidRegistration) {
        super(Collections.emptyList());
        this.kidRegistration = kidRegistration;
    }

    public KidAuthenticationToken(AuthenticatedKid authenticatedKid,
                                  KidAuthenticationToken kidAuth,
                                  List<GrantedAuthority> authorities) {
        super(authorities);
        this.authenticatedKid = authenticatedKid;
        this.kidAuth = kidAuth;
        setAuthenticated(true);
    }

    public KidAuthenticationToken(KidLogin kidLogin) {
        super(Collections.emptyList());
        this.kidLogin = kidLogin;
    }

    public KidRegistration getKidRegistration() {
        return kidRegistration;
    }

    public KidLogin getKidLogin() {
        return kidLogin;
    }

    @Override
    public Object getCredentials() {
        return kidAuth != null ? kidAuth : null;
    }

    @Override
    public Object getPrincipal() {
        return authenticatedKid;
    }
}
