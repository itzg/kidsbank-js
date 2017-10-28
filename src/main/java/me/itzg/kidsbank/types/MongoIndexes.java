package me.itzg.kidsbank.types;

import me.itzg.kidsbank.config.KidsbankProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Component
public class MongoIndexes {

    private final MongoTemplate mongoTemplate;
    private final KidsbankProperties properties;

    @Autowired
    public MongoIndexes(MongoTemplate mongoTemplate, KidsbankProperties kidsbankProperties) {
        this.mongoTemplate = mongoTemplate;
        this.properties = kidsbankProperties;
    }

    @PostConstruct
    public void init() {
        mongoTemplate.indexOps(Parent.class)
                .ensureIndex(new Index()
                                     .on("socialConnections.provider", Sort.Direction.ASC)
                                     .on("socialConnections.user", Sort.Direction.ASC));

        mongoTemplate.indexOps(Kidlink.class)
                .ensureIndex(new Index()
                                     .on("created", Sort.Direction.ASC)
                                     .expire(properties.getKidlinkExpiration(), TimeUnit.SECONDS));

        mongoTemplate.indexOps(Transaction.class)
                .ensureIndex(new Index()
                                     .on(Transaction.FIELD_ACCOUNT_ID, Sort.Direction.ASC)
                                     .on(Transaction.FIELD_WHEN, Sort.Direction.DESC));

        mongoTemplate.indexOps(ScheduledTransaction.class)
                .ensureIndex(new Index()
                                     .on(ScheduledTransaction.FIELD_ACCOUNT_ID, Sort.Direction.ASC));
    }
}
