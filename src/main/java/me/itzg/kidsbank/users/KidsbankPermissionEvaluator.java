package me.itzg.kidsbank.users;

import me.itzg.kidsbank.types.Parent;
import me.itzg.kidsbank.types.Permissions;
import me.itzg.kidsbank.types.Types;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Component
public class KidsbankPermissionEvaluator implements PermissionEvaluator {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public KidsbankPermissionEvaluator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId,
                                 String targetType,
                                 Object permission) {

        final String userId = authentication.getName();

        switch (targetType) {
            case Types.ACCOUNT:

                switch (permission.toString()) {
                    case Permissions.SHARE:
                        return parentHasAccount(userId, targetId.toString());

                    case Permissions.VIEW:
                        //TODO kids can also view their specific account
                        return parentHasAccount(userId, targetId.toString());
                }

                break;
        }

        return false;
    }

    private boolean parentHasAccount(String parentUserId, String accountId) {
        final Query query = Query.query(Criteria.where("_id").is(parentUserId)
                                                .and("accounts").is(accountId));

        return mongoTemplate.exists(query, Parent.class);
    }
}
