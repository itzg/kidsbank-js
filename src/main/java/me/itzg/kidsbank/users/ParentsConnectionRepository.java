package me.itzg.kidsbank.users;

import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.domain.Parent;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ConnectionFactoryLocator connectionFactoryLocator;
    private ConnectionSignUp connectionSignUp;

    @Autowired
    public ParentsConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator, MongoTemplate mongoTemplate) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        final ConnectionKey connectionKey = connection.getKey();

        log.debug("find user IDs from connection provider={}, user={}",
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

        return localUserIds;
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        final Query query = Query.query(
                where("socialConnections.provider").is(providerId)
                        .and("socialConnections.user").in(providerUserIds));
        query.fields().include("_id");

        return mongoTemplate.find(query, Parent.class).stream()
                .map(Parent::getId)
                .collect(Collectors.toSet());
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
