package me.itzg.kidsbank.users;

import me.itzg.kidsbank.types.Kid;
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
 * This component declares the object type permissions referenced in <code>hasPermission</code> usage within
 * {@link org.springframework.security.access.prepost.PreAuthorize} annotations.
 *
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

        final boolean isParent = isParentAuthentication(authentication);
        final String userId = authentication.getName();

        switch (targetType) {
            case Types.ACCOUNT:

                final String accountId = targetId.toString();

                switch (permission.toString()) {
                    case Permissions.SHARE:
                        return parentHasAccount(userId, accountId);

                    case Permissions.VIEW:
                        return (isParent && parentHasAccount(userId, accountId)) ||
                                kidHasAccount(userId, accountId);

                    case Permissions.READ_ENTRIES:
                        return (isParent && parentHasAccount(userId, accountId)) ||
                                kidHasAccount(userId, accountId);

                    case Permissions.MODIFY_ENTRIES:
                        return parentHasAccount(userId, accountId);

                }

                break;
        }

        return false;
    }

    private boolean kidHasAccount(String userId, String accountId) {
        final Query query = Query.query(
                Criteria.where("_id").is(userId)
                        .and(Kid.FIELD_ACCOUNTS).is(accountId)
        );

        return mongoTemplate.exists(query, Kid.class);
    }

    private boolean isParentAuthentication(Authentication authentication) {
        return authentication.getAuthorities().contains(Authorities.PARENT_AUTHORITY);
    }

    private boolean parentHasAccount(String parentUserId, String accountId) {
        final Query query = Query.query(Criteria.where("_id").is(parentUserId)
                                                .and("accounts").is(accountId));

        return mongoTemplate.exists(query, Parent.class);
    }
}
