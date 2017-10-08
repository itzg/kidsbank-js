package me.itzg.kidsbank.services;

import me.itzg.kidsbank.config.KidsbankProperties;
import me.itzg.kidsbank.types.Kidlink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Service
public class KidlinkServiceImpl implements KidlinkService {

    private final KidsbankProperties properties;
    private final Timestamper timestamper;
    private final MongoTemplate mongoTemplate;
    private final CodeGenerator codeGenerator;

    @Autowired
    public KidlinkServiceImpl(CodeGenerator codeGenerator,
                              KidsbankProperties properties,
                              Timestamper timestamper,
                              MongoTemplate mongoTemplate) {
        this.codeGenerator = codeGenerator;
        this.properties = properties;
        this.timestamper = timestamper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @PreAuthorize("hasPermission(#accountId, 'Account', 'share')")
    public String shareAccount(String parentUserId, String accountId) {

        final String code = codeGenerator.generate(properties.getKidlinkDigits());
        final Kidlink kidlink = new Kidlink();
        kidlink.setCode(code);
        kidlink.setAccounts(Collections.singletonList(accountId));
        kidlink.setSharedBy(parentUserId);
        kidlink.setCreated(timestamper.now());

        mongoTemplate.insert(kidlink);

        return code;
    }

    /**
     * Locates and deactivates a sharing code.
     *
     * @param kidlinkCode the kidlink code generated during a share
     * @return the kidlink for the given code or null if the code wasn't valid
     */
    @Override
    public Kidlink useCode(String kidlinkCode) {
        return mongoTemplate.findAndRemove(Query.query(Criteria.where("_id").is(kidlinkCode)), Kidlink.class);
    }
}
