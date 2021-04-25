package me.itzg.kidsbank.repositories;

import me.itzg.kidsbank.types.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    Page<Transaction> findByAccountId(String accountId, Pageable pageable);
}
