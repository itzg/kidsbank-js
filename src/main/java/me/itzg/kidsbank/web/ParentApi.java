package me.itzg.kidsbank.web;

import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.errors.NotFoundException;
import me.itzg.kidsbank.services.AccountsService;
import me.itzg.kidsbank.services.KidlinkService;
import me.itzg.kidsbank.services.TransactionsService;
import me.itzg.kidsbank.types.Account;
import me.itzg.kidsbank.types.AccountCreation;
import me.itzg.kidsbank.types.ResponseValue;
import me.itzg.kidsbank.types.RestoreResults;
import me.itzg.kidsbank.types.Transaction;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@RestController
@RequestMapping(Paths.API + Paths.PARENT)
@Slf4j
public class ParentApi {

    private final AccountsService accountsService;
    private final KidlinkService kidlinkService;
    private TransactionsService transactionsService;

    @Autowired
    public ParentApi(AccountsService accountsService,
                     KidlinkService kidlinkService,
                     TransactionsService transactionsService) {
        this.accountsService = accountsService;
        this.kidlinkService = kidlinkService;
        this.transactionsService = transactionsService;
    }

    @PostMapping("accounts")
    public Account createAccount(Principal principal,
                                 @Validated @RequestBody AccountCreation accountCreation) throws NotFoundException {
        return accountsService.createAccount(principal.getName(), accountCreation);
    }

    @GetMapping("accounts/{accountId}")
    public Account getAccount(@PathVariable String accountId) throws NotFoundException {
        // accountsService will authorize specific access
        return accountsService.getAccount(accountId);
    }

    @PostMapping("accounts/{accountId}/_share")
    public ResponseValue<String> shareSingleAccout(Principal principal, @PathVariable String accountId) {
        return new ResponseValue<>(kidlinkService.shareAccount(principal.getName(), accountId));
    }

    @PostMapping("accounts/{accountId}/transactions")
    public Transaction createTransaction(Principal principal,
                                         @PathVariable String accountId,
                                         @Validated @RequestBody Transaction transactionCreation) {
        return transactionsService.createTransaction(principal.getName(), accountId, transactionCreation);
    }

    @GetMapping("accounts/{accountId}/balance")
    public ResponseValue<Float> getAccountBalance(@PathVariable String accountId) {
        return new ResponseValue<>(accountsService.getBalance(accountId));
    }

    @GetMapping("accounts/{accountId}/transactions")
    public Page<Transaction> getTransactions(Principal principal, @PathVariable String accountId, Pageable pageable) {
        return transactionsService.getTransactions(principal.getName(), accountId, pageable);
    }

    @GetMapping(path = "accounts/{accountId}/backup")
    public ModelAndView backupTransactions(Principal principal, @PathVariable String accountId) {
        Map<String, Object> model = new HashMap<>();
        model.put("transactions", transactionsService.streamAll(accountId));
        return new ModelAndView("transactions", model);
    }

    @PostMapping(value = "accounts/{accountId}/_restore", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestoreResults restoreTransactions(Principal principal, @PathVariable String accountId,
                                              @RequestPart("file") MultipartFile transactionsFile) throws IOException, InvalidFormatException {
        log.debug("User={} restoring={} into accountId={}", principal, transactionsFile, accountId);

        return transactionsService.restore(accountId, transactionsFile);
    }

    @GetMapping("accounts")
    public List<Account> listAccounts(Principal principal) throws NotFoundException {
        return accountsService.getParentManagedAccounts(principal.getName());
    }
}
