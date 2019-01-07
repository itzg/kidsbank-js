package me.itzg.kidsbank.web;

import java.security.Principal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.errors.NotFoundException;
import me.itzg.kidsbank.services.AccountsService;
import me.itzg.kidsbank.services.TransactionsService;
import me.itzg.kidsbank.types.Account;
import me.itzg.kidsbank.types.AccountCreation;
import me.itzg.kidsbank.types.ResponseValue;
import me.itzg.kidsbank.users.ParentOAuth2DetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@RestController
@RequestMapping(Paths.API + Paths.PARENT)
@Slf4j
public class ParentApi {

    private final AccountsService accountsService;
    private TransactionsService transactionsService;
    private final ParentOAuth2DetailsLoader detailsLoader;

    @Autowired
    public ParentApi(AccountsService accountsService,
                     TransactionsService transactionsService,
        ParentOAuth2DetailsLoader detailsLoader) {
        this.accountsService = accountsService;
        this.transactionsService = transactionsService;
        this.detailsLoader = detailsLoader;
    }

    @PostMapping("accounts")
    public Account createAccount(Principal principal,
                                 @Validated @RequestBody AccountCreation accountCreation) throws NotFoundException {
        return accountsService.createAccount(detailsLoader.extractParentId(principal), accountCreation);
    }

    @GetMapping("accounts/{accountId}")
    public Account getAccount(@PathVariable String accountId) throws NotFoundException {
        // accountsService will authorize specific access
        return accountsService.getAccount(accountId);
    }

    @GetMapping("accounts/{accountId}/balance")
    public ResponseValue<Float> getAccountBalance(@PathVariable String accountId) {
        return new ResponseValue<>(accountsService.getBalance(accountId));
    }

    @GetMapping("accounts")
    public List<Account> listAccounts(Principal principal) throws NotFoundException {
        return accountsService.getParentManagedAccounts(detailsLoader.extractParentId(principal));
    }
}
