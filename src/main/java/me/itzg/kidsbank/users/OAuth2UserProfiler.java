package me.itzg.kidsbank.users;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * @author Geoff Bourne
 * @since Dec 2018
 */
@Service
public class OAuth2UserProfiler {

  private final List<OAuth2AttributesConverter> converters;

  @Autowired
  public OAuth2UserProfiler(List<OAuth2AttributesConverter> converters) {
    this.converters = converters;
  }

  public String getDisplayName(OAuth2AuthenticationToken token) {
    for (OAuth2AttributesConverter converter : converters) {
      if (converter.supports(token)) {
        return converter.getDisplayName(token);
      }
    }
    return (String) token.getPrincipal().getAttributes().get("name");
  }

}
