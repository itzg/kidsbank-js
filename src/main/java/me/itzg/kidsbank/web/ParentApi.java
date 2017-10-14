package me.itzg.kidsbank.web;

import me.itzg.kidsbank.errors.NotFoundException;
import me.itzg.kidsbank.services.AccountsService;
import me.itzg.kidsbank.services.KidlinkService;
import me.itzg.kidsbank.services.TransactionsService;
import me.itzg.kidsbank.types.Account;
import me.itzg.kidsbank.types.AccountCreation;
import me.itzg.kidsbank.types.ResponseValue;
import me.itzg.kidsbank.types.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@RestController
@RequestMapping(Paths.API + Paths.PARENT)
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

    @GetMapping("accounts/{accountId}/transactions")
    public Page<Transaction> getTransactions(Principal principal, @PathVariable String accountId, Pageable pageable) {
        return transactionsService.getTransactions(principal.getName(), accountId, pageable);
    }

    @GetMapping("accounts")
    public List<Account> listAccounts(Principal principal) throws NotFoundException {
        return accountsService.getParentManagedAccounts(principal.getName());
    }
}
