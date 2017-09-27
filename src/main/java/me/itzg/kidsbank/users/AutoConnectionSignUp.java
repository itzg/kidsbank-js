package me.itzg.kidsbank.users;

import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.domain.SocialConnection;
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

    @Autowired
    public AutoConnectionSignUp(ParentAccountService parentAccountService) {
        this.parentAccountService = parentAccountService;
    }

    @Override
    public String execute(Connection<?> connection) {
        log.debug("Auto signing up provider={}, user={}",
                  connection.getKey().getProviderId(),
                  connection.getKey().getProviderUserId());

        final SocialConnection socialConnection = new SocialConnection(connection.getKey().getProviderId(),
                                                                       connection.getKey().getProviderUserId());
        socialConnection.setDisplayName(connection.getDisplayName());
        socialConnection.setProfileImageUrl(connection.getImageUrl());
        return parentAccountService.autoCreateUser(socialConnection);
    }
}
