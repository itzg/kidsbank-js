package me.itzg.kidsbank.users;

import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.repositories.ParentRepository;
import me.itzg.kidsbank.types.Parent;
import me.itzg.kidsbank.types.SocialConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Service
@Slf4j
public class ParentAccountService implements SocialUserDetailsService {

    private final ParentRepository parentRepository;

    @Autowired
    public ParentAccountService(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        log.debug("Loading user={}", userId);
        if (!parentRepository.existsById(userId)) {
            log.debug("User={} not found", userId);
            throw new UsernameNotFoundException(userId);
        }

        return new SocialUser(userId, "N/A", Collections.singletonList(Authorities.PARENT_AUTHORITY));
    }

    public String autoCreateUser(SocialConnection socialConnection) {

        final Parent parent = new Parent();
        parent.setSocialConnections(Collections.singletonList(socialConnection));

        final Parent saved = parentRepository.save(parent);

        log.debug("Saved new parent={}", saved);

        return saved.getId();
    }
}
