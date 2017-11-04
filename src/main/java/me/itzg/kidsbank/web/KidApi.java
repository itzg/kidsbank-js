package me.itzg.kidsbank.web;

import me.itzg.kidsbank.errors.NotFoundException;
import me.itzg.kidsbank.services.AccountsService;
import me.itzg.kidsbank.services.TransactionsService;
import me.itzg.kidsbank.types.AccountSummary;
import me.itzg.kidsbank.types.ResponseValue;
import me.itzg.kidsbank.types.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@RestController
@RequestMapping("/api/kid")
public class KidApi {

    private AccountsService accountsService;
    private TransactionsService transactionsService;

    @Autowired
    public KidApi(AccountsService accountsService, TransactionsService transactionsService) {
        this.accountsService = accountsService;
        this.transactionsService = transactionsService;
    }

    @GetMapping("primary-account/balance")
    public ResponseValue<Float> getPrimaryAccountBalance(Principal principal) throws NotFoundException {
        final String accountId = accountsService.getKidPrimaryAccountId(principal.getName());

        return ResponseValue.of(accountsService.getBalance(accountId));
    }

    @GetMapping("primary-account")
    public AccountSummary getPrimaryAccountSummary(Principal principal) throws NotFoundException {
        return accountsService.getKidPrimaryAccountSummary(principal.getName());
    }

    @GetMapping("accounts/{accountId}/transactions")
    public Page<Transaction> getTransactions(@PathVariable String accountId, Pageable pageable) {
        return transactionsService.getTransactions(accountId, pageable);
    }

}
