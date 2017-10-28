package me.itzg.kidsbank.web;

import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.services.KidlinkService;
import me.itzg.kidsbank.types.ResponseValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@RestController
@RequestMapping(Paths.API + Paths.PARENT)
@Slf4j
public class ParentShareApi {

    private KidlinkService kidlinkService;

    @Autowired
    public ParentShareApi(KidlinkService kidlinkService) {
        this.kidlinkService = kidlinkService;
    }

    @PostMapping("accounts/{accountId}/_share")
    public ResponseValue<String> shareSingleAccout(Principal principal, @PathVariable String accountId) {
        return new ResponseValue<>(kidlinkService.shareAccount(principal.getName(), accountId));
    }

}
