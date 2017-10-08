package me.itzg.kidsbank.services;

import me.itzg.kidsbank.types.Kidlink;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public interface KidlinkService {
    @PreAuthorize("hasPermission(#accountId, 'Account', 'share')")
    String shareAccount(String parentUserId, String accountId);

    Kidlink useCode(String kidlinkCode);
}
