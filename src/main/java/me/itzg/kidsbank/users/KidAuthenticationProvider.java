package me.itzg.kidsbank.users;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
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
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Set;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Component
public class KidAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final KidlinkService kidlinkService;
    private final MongoTemplate mongoTemplate;
    private final Validator validator;
    private final Counter kidRegisterSuccess;
    private final Counter kidLoginSuccess;
    private CompositeMeterRegistry meterRegistry;

    @Autowired
    public KidAuthenticationProvider(PasswordEncoder passwordEncoder,
                                     KidlinkService kidlinkService,
                                     MongoTemplate mongoTemplate,
                                     LocalValidatorFactoryBean validatorFactory,
                                     CompositeMeterRegistry meterRegistry) {
        this.passwordEncoder = passwordEncoder;
        this.kidlinkService = kidlinkService;
        this.mongoTemplate = mongoTemplate;
        validator = validatorFactory.getValidator();
        this.meterRegistry = meterRegistry;

        kidRegisterSuccess = meterRegistry.counter("kid_register_success");
        kidLoginSuccess = meterRegistry.counter("kid_login_success");
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

        final Set<ConstraintViolation<KidLogin>> constraintViolations = validator.validate(login);
        processConstraintViolations(constraintViolations);

        final Kid kid = mongoTemplate.findById(login.getUsername(), Kid.class);
        //noinspection ConstantConditions
        if (kid == null) {
            throw new BadCredentialFieldException("Unable to find login info", "username", "Unknown username");
        }

        if (!passwordEncoder.matches(login.getPassword(), kid.getEncPassword())) {
            throw new BadCredentialFieldException("Invalid login", "password", "Wrong password");
        }

        kidLoginSuccess.increment();
        return new KidAuthenticationToken(new AuthenticatedKid(kid),
                                          kidAuth,
                                          Collections.singletonList(Authorities.KID_AUTHORITY));
    }

    private Authentication handleRegistration(KidAuthenticationToken kidAuth) throws AuthenticationException {

        final KidRegistration registration = kidAuth.getKidRegistration();
        final Set<ConstraintViolation<KidRegistration>> constraintViolations = validator.validate(registration);
        processConstraintViolations(constraintViolations);

        final Kid kid = new Kid();
        kid.setUsername(registration.getUsername());
        // optimistically grab the desired username
        try {
            mongoTemplate.insert(kid);
        } catch (DataIntegrityViolationException e) {
            throw new BadCredentialFieldException("Username is already taken", "username", "Username is already taken");
        }

        final Kidlink kidlink = kidlinkService.useCode(registration.getKidlinkCode());
        if (kidlink == null) {
            // optimistic creation of username needs to be undone
            mongoTemplate.remove(kid);
            throw new BadCredentialFieldException("Invalid or expired kidlink code",
                                                  "kidlinkCode",
                                                  "Invalid or expired kidlink code");
        }

        kid.setEncPassword(passwordEncoder.encode(registration.getPassword()));
        registration.setPassword("");
        kid.setAccounts(kidlink.getAccounts());
        kid.setParents(Collections.singletonList(kidlink.getSharedBy()));
        mongoTemplate.save(kid);

        kidRegisterSuccess.increment();
        return new KidAuthenticationToken(new AuthenticatedKid(kid),
                                          kidAuth,
                                          Collections.singletonList(Authorities.KID_AUTHORITY));
    }

    private <T> void processConstraintViolations(Set<ConstraintViolation<T>> constraintViolations) {
        if (!constraintViolations.isEmpty()) {
            final BadCredentialFieldException e = new BadCredentialFieldException(
                    "Failed to validate");
            constraintViolations.forEach(v -> {
                e.addFieldMessage(v.getPropertyPath().toString(), v.getMessage());
            });
            throw e;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return KidAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
