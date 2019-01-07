package me.itzg.kidsbank.users;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

/**
 * @author Geoff Bourne
 * @since Dec 2018
 */
public interface OAuth2AttributesConverter {

  boolean supports(OAuth2AuthenticationToken token);

  String getDisplayName(OAuth2AuthenticationToken token);
}
