package me.itzg.kidsbank.repositories;

import me.itzg.kidsbank.types.ScheduledTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public interface ScheduledTransactionRepository extends MongoRepository<ScheduledTransaction, String> {

    List<ScheduledTransaction> findByAccountId(String accountId);
}
