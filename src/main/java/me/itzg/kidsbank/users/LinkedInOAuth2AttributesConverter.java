package me.itzg.kidsbank.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * @author Geoff Bourne
 * @since Dec 2018
 */
@Component
@Slf4j
public class LinkedInOAuth2AttributesConverter implements OAuth2AttributesConverter {

  public static final String REGISTRATION_ID = "linkedin";
  private final ObjectMapper objectMapper;

  @Autowired
  public LinkedInOAuth2AttributesConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public boolean supports(OAuth2AuthenticationToken token) {
    return token.getAuthorizedClientRegistrationId().equals(REGISTRATION_ID);
  }

  @Override
  public String getDisplayName(OAuth2AuthenticationToken token) {
    final Map<String, Object> attributes = token.getPrincipal().getAttributes();

    return String.format("%s %s",
        getLocalizedAttribute(attributes, "firstName"),
        getLocalizedAttribute(attributes, "lastName")
    );
  }

  private String getLocalizedAttribute(Map<String, Object> attributes, String name) {
    final Object attribute = attributes.get(name);
    if (attribute != null) {
      try {
        final LocalizedAttribute localizedAttribute = objectMapper
            .convertValue(attribute, LocalizedAttribute.class);
        return localizedAttribute.getLocalized().get(
            String.format(
                "%s_%s",
                localizedAttribute.getPreferredLocale().getLanguage(),
                localizedAttribute.getPreferredLocale().getCountry()
            )
        );
      } catch (IllegalArgumentException e) {
        log.warn("Failed to parse into LocalizedAttribute from {} ", attribute, e);
        return "";
      }
    }
    return null;
  }

  @Data
  public static class LocalizedAttribute {

    Map<String, String> localized;
    PreferredLocale preferredLocale;

    @Data
    static class PreferredLocale {

      String country;
      String language;
    }
  }
}
