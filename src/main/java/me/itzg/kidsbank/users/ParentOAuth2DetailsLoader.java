package me.itzg.kidsbank.users;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.security.Principal;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.repositories.ParentRepository;
import me.itzg.kidsbank.types.Parent;
import me.itzg.kidsbank.types.ParentUserDetails;
import me.itzg.kidsbank.types.SocialConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Component
@Slf4j
public class ParentOAuth2DetailsLoader implements OAuth2DetailsLoader {

    private final MongoTemplate mongoTemplate;
    private final ParentRepository parentRepository;
    private final Counter created;
    private final Counter found;
    private MeterRegistry meterRegistry;

    public ParentOAuth2DetailsLoader(MongoTemplate mongoTemplate,
                                       ParentRepository parentRepository,
                                       MeterRegistry meterRegistry) {
        this.mongoTemplate = mongoTemplate;
        this.parentRepository = parentRepository;
        this.meterRegistry = meterRegistry;

        created = meterRegistry.counter("created", "type", "parent");
        found = meterRegistry.counter("found", "type", "parent");
    }

    @Override
    public Object loadUserDetails(String provider, String userId) {
        log.debug("Finding user IDs with connection provider={}, user={}",
                  provider, userId);

        final Query query = Query.query(
                where("socialConnections.provider").is(provider)
                        .and("socialConnections.user").is(userId));

        Parent parent = mongoTemplate.findOne(query, Parent.class);

        if (parent == null) {
            parent = createParent(provider, userId);
        }
        else {
            found.increment();
        }

        meterRegistry.counter("login", "type", "social", "provider", provider)
                .increment();
        log.info("Found parent={} with connection provider={}, user={}",
                 parent, provider, userId);
        return new ParentUserDetails()
            .setId(parent.getId());
    }

    private Parent createParent(String provider, String userId) {
        log.info("Creating new parent with provider={} userId={}",
            provider, userId);
        created.increment();
        return parentRepository.save(
            new Parent()
            .setSocialConnections(Collections.singletonList(
                new SocialConnection(provider, userId)
            ))
        );
    }

    public String extractParentId(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken) {
            return ((ParentUserDetails) ((OAuth2AuthenticationToken) principal).getDetails()).getId();
        }
        else {
            return principal.getName();
        }
    }
}
