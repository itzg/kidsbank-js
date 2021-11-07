package me.itzg.kidsbank.services;

import com.mongodb.bulk.BulkWriteResult;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.io.IOException;
import java.util.List;
import me.itzg.kidsbank.errors.NotFoundException;
import me.itzg.kidsbank.repositories.TransactionRepository;
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
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Service
public class TransactionsService {

    private final Counter cashFlowCounter;
    private final Counter transactionsCounter;
    private MongoTemplate mongoTemplate;
    private TransactionsFileProcessor transactionsFileProcessor;
    private TransactionRepository repository;

    @Autowired
    public TransactionsService(MongoTemplate mongoTemplate,
                               TransactionsFileProcessor transactionsFileProcessor,
                               TransactionRepository repository,
                               MeterRegistry meterRegistry) {
        this.mongoTemplate = mongoTemplate;
        this.transactionsFileProcessor = transactionsFileProcessor;
        this.repository = repository;

        cashFlowCounter = meterRegistry.counter("cash_flow");
        transactionsCounter = meterRegistry.counter("transactions");
    }

    @PreAuthorize("hasPermission(#transaction.accountId, 'Account', 'modifyEntries')")
    public Transaction createTransaction(Transaction transaction) {

        Assert.isNull(transaction.getId(), "This transaction already has an ID");

        transactionsCounter.increment();
        cashFlowCounter.increment(Math.abs(transaction.getAmount()));
        return repository.save(transaction);
    }

    @PreAuthorize("hasPermission(#accountId, 'Account', 'readEntries')")
    public Page<Transaction> getTransactions(String accountId, Pageable pageable) {
        final Query baseQuery = new Query(new Criteria(Transaction.FIELD_ACCOUNT_ID).is(accountId));

        final long total = mongoTemplate.count(baseQuery, Transaction.class);

        final List<Transaction> results = mongoTemplate.find(baseQuery
                                                                     .with(Sort.by(Sort.Direction.DESC,
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
            transactionsCounter.increment();
            cashFlowCounter.increment(Math.abs(transaction.getAmount()));
            transaction.setAccountId(accountId);
            transaction.setCreationType(Transaction.CreationType.RESTORE);
        });

        final BulkWriteResult bulkResult = bulkOp.insert(transactions).execute();

        final RestoreResults restoreResults = new RestoreResults();
        restoreResults.setProcessed(bulkResult.getInsertedCount());

        return restoreResults;
    }

    @PreAuthorize("hasPermission(#transaction.accountId, 'Account', 'modifyEntries')")
    public Transaction save(String userId, Transaction transaction) throws NotFoundException {
        final String tid = transaction.getId();
        Assert.notNull(tid, "Missing transactionId");

        final Transaction stored = repository.findById(tid)
                .orElseThrow(() -> new NotFoundException(String.format("Transaction %s does not exist",
                                                                       tid)));

        stored.setModifiedBy(userId);
        stored.setCreationType(Transaction.CreationType.NORMAL);
        stored.setDescription(transaction.getDescription());
        stored.setWhen(transaction.getWhen());
        stored.setAmount(transaction.getAmount());

        // don't adjust cashflow counter since we'd also need the previous value...not worth the precision

        return repository.save(stored);
    }

    @PreAuthorize("hasPermission(#transactionId, 'Transaction', 'delete')")
    public void delete(String transactionId) {
        repository.deleteById(transactionId);
    }
}
