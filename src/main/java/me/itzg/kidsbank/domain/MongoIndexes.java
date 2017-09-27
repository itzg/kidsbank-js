package me.itzg.kidsbank.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Component
public class MongoIndexes {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoIndexes(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void init() {
        mongoTemplate.indexOps(Parent.class)
                .ensureIndex(new Index()
                                     .on("socialConnections.provider", Sort.Direction.ASC)
                                     .on("socialConnections.user", Sort.Direction.ASC));
    }
}
