package me.itzg.kidsbank.users;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.types.SocialConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Component
@Slf4j
public class AutoConnectionSignUp implements ConnectionSignUp {

    private final ParentAccountService parentAccountService;
    private final CompositeMeterRegistry meterRegistry;

    @Autowired
    public AutoConnectionSignUp(ParentAccountService parentAccountService,
                                CompositeMeterRegistry meterRegistry) {
        this.parentAccountService = parentAccountService;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public String execute(Connection<?> connection) {
        log.debug("Auto signing up provider={}, user={}",
                  connection.getKey().getProviderId(),
                  connection.getKey().getProviderUserId());

        meterRegistry.counter("social_signup", "provider", connection.getKey().getProviderId())
                .increment();

        final SocialConnection socialConnection = new SocialConnection(connection.getKey().getProviderId(),
                                                                       connection.getKey().getProviderUserId());
        socialConnection.setDisplayName(connection.getDisplayName());
        socialConnection.setProfileImageUrl(connection.getImageUrl());
        return parentAccountService.autoCreateUser(socialConnection);
    }
}
