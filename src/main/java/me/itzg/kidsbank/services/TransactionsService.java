package me.itzg.kidsbank.services;

import com.mongodb.bulk.BulkWriteResult;
import me.itzg.kidsbank.config.KidsbankProperties;
import me.itzg.kidsbank.types.RestoreResults;
import me.itzg.kidsbank.types.Transaction;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Service
public class TransactionsService {

    private MongoTemplate mongoTemplate;
    private KidsbankProperties properties;
    private TransactionsFileProcessor transactionsFileProcessor;

    @Autowired
    public TransactionsService(MongoTemplate mongoTemplate, KidsbankProperties properties,
                               TransactionsFileProcessor transactionsFileProcessor) {
        this.mongoTemplate = mongoTemplate;
        this.properties = properties;
        this.transactionsFileProcessor = transactionsFileProcessor;
    }

    @PreAuthorize("hasPermission(#accountId, 'Account', 'modifyEntries')")
    public Transaction createTransaction(String parentId, String accountId, Transaction transactionCreation) {

        final Transaction transaction = new Transaction();
        transaction.setWhen(transactionCreation.getWhen());
        transaction.setDescription(transactionCreation.getDescription());
        transaction.setAmount(transactionCreation.getAmount());
        transaction.setCreatedBy(parentId);
        transaction.setAccountId(accountId);

        mongoTemplate.insert(transaction);

        return transaction;
    }

    @PreAuthorize("hasPermission(#accountId, 'Account', 'readEntries')")
    public Page<Transaction> getTransactions(String parentId, String accountId, Pageable pageable) {
        final Query baseQuery = new Query(new Criteria(Transaction.FIELD_ACCOUNT_ID).is(accountId));

        final long total = mongoTemplate.count(baseQuery, Transaction.class);

        final List<Transaction> results = mongoTemplate.find(baseQuery
                                                                     .with(new Sort(Sort.Direction.DESC,
                                                                                    Transaction.FIELD_WHEN))
                                                                     .with(pageable), Transaction.class);

        return new PageImpl<>(results, pageable, total);
    }

    @PreAuthorize("hasPermission(#accountId, 'Account', 'readEntries')")
    public CloseableIterator<Transaction> streamAll(String accountId) {
        return mongoTemplate.stream(new Query(new Criteria(Transaction.FIELD_ACCOUNT_ID).is(accountId)),
                                    Transaction.class);
    }

    @PreAuthorize("hasPermission(#accountId, 'Account', 'modifyEntries')")
    public RestoreResults restore(String accountId,
                                  MultipartFile transactionsFile) throws IOException, InvalidFormatException {

        final List<Transaction> transactions = transactionsFileProcessor.process(transactionsFile);

        final BulkOperations bulkOp = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
                                                            Transaction.class);

        transactions.forEach(transaction -> {
            transaction.setAccountId(accountId);
            transaction.setCreationType(Transaction.CreationType.RESTORE);
        });

        final BulkWriteResult bulkResult = bulkOp.insert(transactions).execute();

        final RestoreResults restoreResults = new RestoreResults();
        restoreResults.setProcessed(bulkResult.getInsertedCount());

        return restoreResults;
    }
}
