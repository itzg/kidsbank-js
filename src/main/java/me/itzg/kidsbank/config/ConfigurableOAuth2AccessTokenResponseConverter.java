package me.itzg.kidsbank.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.util.StringUtils;

/**
 * "Derived" from org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter.OAuth2AccessTokenResponseConverter
 * but with accessTokenType defaulted to {@link TokenType#BEARER}.
 * <p>
 * This is needed because LinkedIn's OAuth2 access token response violates the OAuth2 spec
 * https://docs.microsoft.com/en-us/linkedin/shared/authentication/authorization-code-flow?context=linkedin/consumer/context#step-3-exchange-authorization-code-for-an-access-token
 * </p>
 * <p>
 * This is the recommended work around according to https://github.com/spring-projects/spring-security/issues/5983
 * </p>
 */
class ConfigurableOAuth2AccessTokenResponseConverter implements
    Converter<Map<String, String>, OAuth2AccessTokenResponse> {
  private static final Set<String> TOKEN_RESPONSE_PARAMETER_NAMES = (Set) Stream
      .of("access_token", "token_type", "expires_in", "refresh_token", "scope").collect(Collectors.toSet());

  private TokenType defaultAccessTokenType;

  ConfigurableOAuth2AccessTokenResponseConverter() {
  }

  public OAuth2AccessTokenResponse convert(Map<String, String> tokenResponseParameters) {
    String accessToken = (String)tokenResponseParameters.get("access_token");
    TokenType accessTokenType = defaultAccessTokenType;
    if (TokenType.BEARER.getValue().equalsIgnoreCase((String)tokenResponseParameters.get("token_type"))) {
      accessTokenType = TokenType.BEARER;
    }

    long expiresIn = 0L;
    if (tokenResponseParameters.containsKey("expires_in")) {
      try {
        expiresIn = Long.valueOf((String)tokenResponseParameters.get("expires_in"));
      } catch (NumberFormatException var9) {
      }
    }

    Set<String> scopes = Collections.emptySet();
    String refreshToken;
    if (tokenResponseParameters.containsKey("scope")) {
      refreshToken = (String)tokenResponseParameters.get("scope");
      scopes = (Set) Arrays.stream(StringUtils.delimitedListToStringArray(refreshToken, " ")).collect(Collectors.toSet());
    }

    refreshToken = (String)tokenResponseParameters.get("refresh_token");
    Map<String, Object> additionalParameters = new LinkedHashMap();
    tokenResponseParameters.entrySet().stream().filter((e) -> {
      return !TOKEN_RESPONSE_PARAMETER_NAMES.contains(e.getKey());
    }).forEach((e) -> {
      additionalParameters.put(e.getKey(), e.getValue());
    });
    return OAuth2AccessTokenResponse.withToken(accessToken).tokenType(accessTokenType).expiresIn(expiresIn).scopes(scopes).refreshToken(refreshToken).additionalParameters(additionalParameters).build();
  }

  public TokenType getDefaultAccessTokenType() {
    return defaultAccessTokenType;
  }

  public void setDefaultAccessTokenType(
      TokenType defaultAccessTokenType) {
    this.defaultAccessTokenType = defaultAccessTokenType;
  }
}
