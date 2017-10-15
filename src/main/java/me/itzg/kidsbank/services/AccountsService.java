package me.itzg.kidsbank.services;

import com.mongodb.client.result.UpdateResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.errors.NotFoundException;
import me.itzg.kidsbank.types.Account;
import me.itzg.kidsbank.types.AccountCreation;
import me.itzg.kidsbank.types.Kid;
import me.itzg.kidsbank.types.Parent;
import me.itzg.kidsbank.types.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static me.itzg.kidsbank.types.Transaction.FIELD_ACCOUNT_ID;
import static me.itzg.kidsbank.types.Transaction.FIELD_AMOUNT;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Service
@Slf4j
public class AccountsService {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AccountsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Account> getParentManagedAccounts(String parentUserId) throws NotFoundException {

        final Query query = query(where("_id").is(parentUserId));
        query.fields().include("accounts");

        final Parent parent = mongoTemplate.findOne(
                query,
                Parent.class);

        //noinspection ConstantConditions
        if (parent == null) {
            throw new NotFoundException("parent account");
        }

        if (parent.getAccounts() == null || parent.getAccounts().isEmpty()) {
            return Collections.emptyList();
        }

        return mongoTemplate.find(query(where("_id").in(parent.getAccounts())), Account.class);
    }

    public Account createAccount(String parentUserId, AccountCreation accountCreation) throws NotFoundException {
        log.debug("Creating account for parent={} with details={}", parentUserId, accountCreation);

        final Account account = new Account();
        account.setName(accountCreation.getName());

        mongoTemplate.insert(account);

        final UpdateResult updateResult = mongoTemplate.updateFirst(query(where("_id").is(parentUserId)),
                                                                    new Update().addToSet("accounts", account.getId()),
                                                                    Parent.class);

        if (updateResult.getMatchedCount() != 1) {
            throw new NotFoundException(String.format("parent not found: %s", parentUserId));
        } else if (updateResult.getModifiedCount() != 1) {
            throw new IllegalStateException(String.format("couldn't add account=%s to parent=%s",
                                                          account.getId(),
                                                          parentUserId));
        }

        if (log.isDebugEnabled()) {
            log.debug("Created account for parent={} with details={} and id={}",
                      parentUserId,
                      accountCreation,
                      account.getId());
        }
        return account;
    }

    @PreAuthorize("hasPermission(#accountId, 'Account', 'view')")
    public Account getAccount(String accountId) throws NotFoundException {
        final Account account = mongoTemplate.findById(accountId, Account.class);
        //noinspection ConstantConditions
        if (account == null) {
            throw new NotFoundException(String.format("account not found: %s", accountId));
        }
        return account;
    }

    @PreAuthorize("hasPermission(#accountId, 'Account', 'readEntries')")
    public float getBalance(String accountId) {

        final Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(FIELD_ACCOUNT_ID).is(accountId)),
                group(FIELD_ACCOUNT_ID).sum(FIELD_AMOUNT).as("balance")
        );

        final AggregationResults<BalanceResult> result =
                mongoTemplate.aggregate(aggregation, Transaction.class, BalanceResult.class);

        if (result.getMappedResults().isEmpty()) {
            return 0;
        }

        return result.getMappedResults().get(0).getBalance();
    }

    public float getKidPrimaryAccountBalance(String kidUserId) throws NotFoundException {

        final Kid kid = mongoTemplate.findById(kidUserId, Kid.class);
        //noinspection ConstantConditions
        if (kid == null) {
            throw new NotFoundException(String.format("Unable to find kid account: %s", kidUserId));
        }

        if (kid.getAccounts().isEmpty()) {
            log.warn("Kid account={} had no accounts", kidUserId);
            return 0;
        }

        return getBalance(kid.getAccounts().get(0));
    }

    @Data
    private static class BalanceResult {
        float balance;
    }
}
