package me.itzg.kidsbank.users;

import me.itzg.kidsbank.errors.BadCredentialFieldException;
import me.itzg.kidsbank.services.KidlinkService;
import me.itzg.kidsbank.types.Kid;
import me.itzg.kidsbank.types.KidLogin;
import me.itzg.kidsbank.types.KidRegistration;
import me.itzg.kidsbank.types.Kidlink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Component
public class KidAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final KidlinkService kidlinkService;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public KidAuthenticationProvider(PasswordEncoder passwordEncoder,
                                     KidlinkService kidlinkService,
                                     MongoTemplate mongoTemplate) {
        this.passwordEncoder = passwordEncoder;
        this.kidlinkService = kidlinkService;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication instanceof KidAuthenticationToken) {
            final KidAuthenticationToken kidAuth = (KidAuthenticationToken) authentication;

            if (kidAuth.getKidRegistration() != null) {
                return handleRegistration(kidAuth);
            }

            if (kidAuth.getKidLogin() != null) {
                return handleLogin(kidAuth);
            }
        }
        return null;
    }

    private Authentication handleLogin(KidAuthenticationToken kidAuth) throws AuthenticationException {
        final KidLogin login = kidAuth.getKidLogin();
        //TODO replace these with Spring validation
        if (StringUtils.isEmpty(login.getUsername())) {
            throw new BadCredentialFieldException("Missing username", "username");
        }
        if (StringUtils.isEmpty(login.getPassword())) {
            throw new BadCredentialFieldException("Missing password", "password");
        }

        final Kid kid = mongoTemplate.findById(login.getUsername(), Kid.class);
        //noinspection ConstantConditions
        if (kid == null) {
            throw new BadCredentialFieldException("Unknown username", "username");
        }

        if (!passwordEncoder.matches(login.getPassword(), kid.getEncPassword())) {
            throw new BadCredentialFieldException("Wrong password", "password");
        }

        return new KidAuthenticationToken(new AuthenticatedKid(kid),
                                          kidAuth,
                                          Collections.singletonList(Authorities.KID_AUTHORITY));
    }

    private Authentication handleRegistration(KidAuthenticationToken kidAuth) throws AuthenticationException {

        final KidRegistration registration = kidAuth.getKidRegistration();

        if (StringUtils.isEmpty(registration.getUsername())) {
            throw new BadCredentialFieldException("Missing username for registration", "username");
        }

        if (StringUtils.isEmpty(registration.getPassword())) {
            throw new BadCredentialFieldException("Missing password for registration", "password");
        }

        if (StringUtils.isEmpty(registration.getKidlinkCode())) {
            throw new BadCredentialFieldException("Missing kidlink code for registration", "kidlinkCode");
        }

        final Kid kid = new Kid();
        kid.setUsername(registration.getUsername());
        // optimistically grab the desired username
        try {
            mongoTemplate.insert(kid);
        } catch (DataIntegrityViolationException e) {
            throw new BadCredentialFieldException("Username is already taken", "username");
        }

        final Kidlink kidlink = kidlinkService.useCode(registration.getKidlinkCode());
        if (kidlink == null) {
            // optimistic creation of username needs to be undone
            mongoTemplate.remove(kid);
            throw new BadCredentialFieldException("Invalid or expired kidlink code", "kidlinkCode");
        }

        kid.setEncPassword(passwordEncoder.encode(registration.getPassword()));
        registration.setPassword("");
        kid.setAccounts(kidlink.getAccounts());
        kid.setParents(Collections.singletonList(kidlink.getSharedBy()));
        mongoTemplate.save(kid);

        return new KidAuthenticationToken(new AuthenticatedKid(kid),
                                          kidAuth,
                                          Collections.singletonList(Authorities.KID_AUTHORITY));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return KidAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
