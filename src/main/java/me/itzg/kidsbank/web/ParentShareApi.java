package me.itzg.kidsbank.web;

import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.services.KidlinkService;
import me.itzg.kidsbank.types.ResponseValue;
import me.itzg.kidsbank.users.ParentOAuth2DetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@RestController
@RequestMapping(Paths.API + Paths.PARENT)
@Slf4j
public class ParentShareApi {

    private KidlinkService kidlinkService;
    private final ParentOAuth2DetailsLoader detailsLoader;

    @Autowired
    public ParentShareApi(KidlinkService kidlinkService,
        ParentOAuth2DetailsLoader detailsLoader) {
        this.kidlinkService = kidlinkService;
        this.detailsLoader = detailsLoader;
    }

    @PostMapping("accounts/{accountId}/_share")
    public ResponseValue<String> shareSingleAccout(Principal principal, @PathVariable String accountId) {
        return new ResponseValue<>(kidlinkService.shareAccount(detailsLoader.extractParentId(principal),
            accountId));
    }

}
