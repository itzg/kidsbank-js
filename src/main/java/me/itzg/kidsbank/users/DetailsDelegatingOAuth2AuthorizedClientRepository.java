package me.itzg.kidsbank.users;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.util.Assert;

/**
 * @author Geoff Bourne
 * @since Dec 2018
 */
@Slf4j
public class DetailsDelegatingOAuth2AuthorizedClientRepository implements
    OAuth2AuthorizedClientRepository {

  private final OAuth2AuthorizedClientRepository delegate;
  private final OAuth2DetailsLoader detailsLoader;

  public DetailsDelegatingOAuth2AuthorizedClientRepository(
      OAuth2AuthorizedClientRepository delegate,
      OAuth2DetailsLoader detailsLoader) {
    Assert.notNull(delegate, "delegate is required");
    Assert.notNull(detailsLoader, "detailsLoader is required");
    this.delegate = delegate;
    this.detailsLoader = detailsLoader;
  }

  @Override
  public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId,
      Authentication principal, HttpServletRequest request) {
    return delegate.loadAuthorizedClient(clientRegistrationId, principal, request);
  }

  @Override
  public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient,
      Authentication principal, HttpServletRequest request, HttpServletResponse response) {

    if (principal instanceof OAuth2AuthenticationToken) {
      OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;

      final String provider = oauthToken.getAuthorizedClientRegistrationId();
      final String userId = oauthToken.getName();

      final Object details = detailsLoader.loadUserDetails(provider, userId);
      log.debug("Found details={} for social login provider={}, userId={}",
          details, provider, userId);

      oauthToken.setDetails(details);
    }

    delegate.saveAuthorizedClient(authorizedClient, principal, request, response);
  }

  @Override
  public void removeAuthorizedClient(String clientRegistrationId, Authentication principal,
      HttpServletRequest request, HttpServletResponse response) {
    delegate.removeAuthorizedClient(clientRegistrationId, principal, request, response);
  }
}
