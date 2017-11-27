package me.itzg.kidsbank.users;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.types.Parent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryConnectionRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Slf4j
public class ParentsConnectionRepository implements UsersConnectionRepository {

    private final MongoTemplate mongoTemplate;
    private CompositeMeterRegistry meterRegistry;
    private final ConnectionFactoryLocator connectionFactoryLocator;
    private ConnectionSignUp connectionSignUp;

    public ParentsConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator, MongoTemplate mongoTemplate,
                                       CompositeMeterRegistry meterRegistry) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.mongoTemplate = mongoTemplate;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        final ConnectionKey connectionKey = connection.getKey();

        log.debug("Finding user IDs with connection provider={}, user={}",
                  connectionKey.getProviderId(), connectionKey.getProviderUserId());

        final Query query = Query.query(
                where("socialConnections.provider").is(connectionKey.getProviderId())
                        .and("socialConnections.user").is(connectionKey.getProviderUserId()));
        query.fields().include("_id");

        final List<String> localUserIds = mongoTemplate.find(query, Parent.class).stream()
                .map(Parent::getId)
                .collect(Collectors.toList());

        if (localUserIds.size() == 0 && connectionSignUp != null) {
            log.debug("No users found from connection provider={}, user={}, signing up",
                      connectionKey.getProviderId(), connectionKey.getProviderUserId());
            String newUserId = connectionSignUp.execute(connection);
            if (newUserId != null) {
                createConnectionRepository(newUserId).addConnection(connection);
                return Collections.singletonList(newUserId);
            }
        }

        meterRegistry.counter("social_login", "provider", connectionKey.getProviderId())
                .increment();
        log.info("Found user IDs={} with connection provider={}, user={}",
                 localUserIds, connectionKey.getProviderId(), connectionKey.getProviderUserId());
        return localUserIds;
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        log.debug("Finding user IDs connected to provider={}, userIds={}",
                  providerId, providerUserIds);

        final Query query = Query.query(
                where("socialConnections.provider").is(providerId)
                        .and("socialConnections.user").in(providerUserIds));
        query.fields().include("_id");

        final Set<String> ids = mongoTemplate.find(query, Parent.class).stream()
                .map(Parent::getId)
                .collect(Collectors.toSet());

        log.debug("Found user IDs={} connected to provider={}, userIds={}",
                  ids, providerId, providerUserIds);
        return ids;
    }

    @Override
    public ConnectionRepository createConnectionRepository(String userId) {
        return new InMemoryConnectionRepository(connectionFactoryLocator);
    }

    @Override
    public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
        this.connectionSignUp = connectionSignUp;
    }
}
