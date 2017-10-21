package me.itzg.kidsbank.web;

import me.itzg.kidsbank.errors.NotFoundException;
import me.itzg.kidsbank.services.AccountsService;
import me.itzg.kidsbank.types.ResponseValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    public KidApi(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @GetMapping("primary-account/balance")
    public ResponseValue<Float> getPrimaryAccountBalance(Principal principal) throws NotFoundException {
        final String accountId = accountsService.getKidPrimaryAccountId(principal.getName());

        return new ResponseValue<>(accountsService.getBalance(accountId));
    }
}
