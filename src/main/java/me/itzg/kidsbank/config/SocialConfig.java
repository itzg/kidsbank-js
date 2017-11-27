package me.itzg.kidsbank.config;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import me.itzg.kidsbank.users.AutoConnectionSignUp;
import me.itzg.kidsbank.users.ParentsConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Configuration
public class SocialConfig extends SocialConfigurerAdapter {

    private final AutoConnectionSignUp connectionSignUp;
    private final MongoTemplate mongoTemplate;
    private CompositeMeterRegistry meterRegistry;

    @Autowired
    public SocialConfig(AutoConnectionSignUp connectionSignUp,
                        MongoTemplate mongoTemplate,
                        CompositeMeterRegistry meterRegistry) {
        this.connectionSignUp = connectionSignUp;
        this.mongoTemplate = mongoTemplate;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        final ParentsConnectionRepository parentsConnectionRepository = new ParentsConnectionRepository(
                connectionFactoryLocator,
                mongoTemplate,
                meterRegistry);
        parentsConnectionRepository.setConnectionSignUp(connectionSignUp);
        return parentsConnectionRepository;
    }
}
