package me.itzg.kidsbank.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Component
@Slf4j
public class SignInService implements SignInAdapter {
    @Override
    public String signIn(String userId, Connection<?> connection, NativeWebRequest req) {
        log.warn("NOT IMPLEMENTED: userId={}, connection={}, req={}",
                 userId, connection, req);
        return "/";
    }
}
