package me.itzg.kidsbank.web;

import java.io.IOException;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.errors.NotFoundException;
import me.itzg.kidsbank.services.TransactionsService;
import me.itzg.kidsbank.types.ForCreate;
import me.itzg.kidsbank.types.ForSave;
import me.itzg.kidsbank.types.RestoreResults;
import me.itzg.kidsbank.types.Transaction;
import me.itzg.kidsbank.users.ParentOAuth2DetailsLoader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@RestController
@RequestMapping(Paths.API + Paths.PARENT)
@Slf4j
public class ParentTransactionsApi {

    private TransactionsService transactionsService;
    private final ParentOAuth2DetailsLoader detailsLoader;

    @Autowired
    public ParentTransactionsApi(TransactionsService transactionsService,
        ParentOAuth2DetailsLoader detailsLoader) {
        this.transactionsService = transactionsService;
        this.detailsLoader = detailsLoader;
    }

    @PostMapping("accounts/{accountId}/transactions")
    public Transaction createTransaction(Principal principal,
                                         @PathVariable String accountId,
                                         @Validated(ForCreate.class) @RequestBody Transaction transactionCreation) {
        transactionCreation.setAccountId(accountId);
        transactionCreation.setCreatedBy(detailsLoader.extractParentId(principal));
        return transactionsService.createTransaction(transactionCreation);
    }

    @GetMapping("accounts/{accountId}/transactions")
    public Page<Transaction> getTransactions(@PathVariable String accountId, Pageable pageable) {
        return transactionsService.getTransactions(accountId, pageable);
    }

    @PutMapping("transactions")
    public Transaction saveTransaction(Principal principal,
                                       @Validated(ForSave.class) @RequestBody Transaction transaction) throws NotFoundException {
        return transactionsService.save(detailsLoader.extractParentId(principal), transaction);
    }

    @DeleteMapping("transactions/{transactionId}")
    public void deleteTransaction(@PathVariable String transactionId) {
        transactionsService.delete(transactionId);
    }

    @PostMapping(value = "accounts/{accountId}/_import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestoreResults restoreTransactions(Principal principal, @PathVariable String accountId,
                                              @RequestPart("file") MultipartFile transactionsFile) throws IOException, InvalidFormatException {
        log.debug("User={} restoring={} into accountId={}", principal, transactionsFile, accountId);

        return transactionsService.restore(accountId, transactionsFile);
    }

}
