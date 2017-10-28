package me.itzg.kidsbank.users;

import me.itzg.kidsbank.types.Kid;
import me.itzg.kidsbank.types.Parent;
import me.itzg.kidsbank.types.Permissions;
import me.itzg.kidsbank.types.ScheduledTransaction;
import me.itzg.kidsbank.types.Transaction;
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
                        return isParent && parentHasAccount(userId, accountId);

                    case Permissions.VIEW:
                        return (isParent && parentHasAccount(userId, accountId)) ||
                                kidHasAccount(userId, accountId);

                    case Permissions.READ_ENTRIES:
                        return (isParent && parentHasAccount(userId, accountId)) ||
                                kidHasAccount(userId, accountId);

                    case Permissions.MODIFY_ENTRIES:
                        return isParent && parentHasAccount(userId, accountId);

                    default:
                        return false;
                }

            case Types.SCHEDULED_TRANSACTION:
                final String scheduledId = targetId.toString();

                switch (permission.toString()) {
                    case Permissions.DELETE:
                        return isParent && parentHasAccount(userId, getAccountOfScheduled(scheduledId));
                }
                break;

            case Types.TRANSACTION:
                final String transactionId = targetId.toString();

                switch (permission.toString()) {
                    case Permissions.DELETE:
                        return isParent && parentHasAccount(userId, getAccountOfTransaction(transactionId));
                }

                break;
        }

        return false;
    }

    private String getAccountOfScheduled(String scheduledId) {
        final ScheduledTransaction scheduledTransaction =
                mongoTemplate.findById(scheduledId, ScheduledTransaction.class);

        //noinspection ConstantConditions
        if (scheduledTransaction == null) {
            return null;
        }

        return scheduledTransaction.getAccountId();
    }

    private String getAccountOfTransaction(String transactionId) {
        final Transaction transaction =
                mongoTemplate.findById(transactionId, Transaction.class);

        //noinspection ConstantConditions
        if (transaction == null) {
            return null;
        }

        return transaction.getAccountId();
    }

    private boolean kidHasAccount(String userId, String accountId) {
        if (accountId == null) {
            return false;
        }

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
        if (accountId == null) {
            return false;
        }

        final Query query = Query.query(Criteria.where("_id").is(parentUserId)
                                                .and("accounts").is(accountId));

        return mongoTemplate.exists(query, Parent.class);
    }
}
