package me.itzg.kidsbank.config;

import static java.lang.String.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import me.itzg.kidsbank.users.Authorities;
import me.itzg.kidsbank.users.DetailsDelegatingOAuth2AuthorizedClientRepository;
import me.itzg.kidsbank.users.ImpersonateAuthFilter;
import me.itzg.kidsbank.users.KidAuthenticationProvider;
import me.itzg.kidsbank.users.KidLoginAuthFilter;
import me.itzg.kidsbank.users.KidRegisterAuthFilter;
import me.itzg.kidsbank.users.OAuth2DetailsLoader;
import me.itzg.kidsbank.web.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.client.RestTemplate;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final ObjectMapper objectMapper;
  private final KidAuthenticationProvider kidAuthenticationProvider;
  private final RestTemplateBuilder restTemplateBuilder;
  private final OAuth2DetailsLoader detailsLoader;
  private final OAuth2AuthorizedClientRepository authorizedClientRepository;
  private Environment env;

  @Autowired
  public WebSecurityConfig(ObjectMapper objectMapper,
      @Nullable KidAuthenticationProvider kidAuthenticationProvider,
      Environment env,
      @Nullable RestTemplateBuilder restTemplateBuilder,
      @Nullable OAuth2DetailsLoader detailsLoader,
      @Nullable OAuth2AuthorizedClientRepository authorizedClientRepository) {
    this.objectMapper = objectMapper;
    this.kidAuthenticationProvider = kidAuthenticationProvider;
    this.env = env;
    this.restTemplateBuilder = restTemplateBuilder;
    this.detailsLoader = detailsLoader;
    this.authorizedClientRepository = authorizedClientRepository;
  }

  @SuppressWarnings("RedundantThrows")
  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring()
        .antMatchers(Paths.ROOT)
        .antMatchers("/index.html")
        .antMatchers("/favicon.ico")
        .antMatchers("/static/**")
        .antMatchers("/*.json")
        .antMatchers("/*.js");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests()
          .antMatchers(format("%s/**", Paths.API_PARENT)).hasRole(Authorities.ROLE_PARENT)
          .antMatchers(format("%s/**", Paths.API_KID)).hasRole(Authorities.ROLE_KID)
          .antMatchers(format("%s/%s", Paths.API, Paths.CURRENT_USER)).permitAll()
          .antMatchers(format("%s/**", Paths.SIGNIN)).permitAll()
          .antMatchers("/error").permitAll()
          .antMatchers(Paths.ROOT).permitAll()
          .anyRequest().authenticated()
        .and()
        .logout().deleteCookies("JSESSIONID").logoutUrl(Paths.LOGOUT).logoutSuccessUrl(Paths.ROOT)
    ;

    if (authorizedClientRepository != null && detailsLoader != null && restTemplateBuilder != null) {
      http
          .oauth2Login()
            .loginPage(Paths.ROOT)
            .userInfoEndpoint()
              .userService(userService())
              .userAuthoritiesMapper(userAuthoritiesMapper())
            .and()
            .tokenEndpoint().accessTokenResponseClient(accessTokenResponseClient())
          .and()
          .authorizedClientRepository(new DetailsDelegatingOAuth2AuthorizedClientRepository(
              authorizedClientRepository,
              detailsLoader
          ))
          ;
    }

    if (kidAuthenticationProvider != null) {
      http
          .addFilterBefore(kidRegistrationFilter(), AbstractPreAuthenticatedProcessingFilter.class)
          .addFilterBefore(kidLoginFilter(), AbstractPreAuthenticatedProcessingFilter.class)
          ;
    }

    if (env.acceptsProfiles(Profiles.of(KidsbankProfiles.IMPERSONATE))) {
            http.addFilterBefore(new ImpersonateAuthFilter("/api/**"),
                OAuth2LoginAuthenticationFilter.class);
    }
  }

  private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
    final DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();

    final OAuth2AccessTokenResponseHttpMessageConverter messageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
    final ConfigurableOAuth2AccessTokenResponseConverter tokenResponseConverter = new ConfigurableOAuth2AccessTokenResponseConverter();
    tokenResponseConverter.setDefaultAccessTokenType(TokenType.BEARER);
    messageConverter.setTokenResponseConverter(tokenResponseConverter);

    RestTemplate restTemplate = new RestTemplate(Arrays.asList(
        new FormHttpMessageConverter(), messageConverter));
    restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
    tokenResponseClient.setRestOperations(restTemplate);

    return tokenResponseClient;
  }

  private KidLoginAuthFilter kidLoginFilter() throws Exception {
    final KidLoginAuthFilter filter = new KidLoginAuthFilter(Paths.KID_LOGIN, objectMapper);
    filter.setAuthenticationManager(authenticationManager());
    return filter;
  }

  private KidRegisterAuthFilter kidRegistrationFilter() throws Exception {
    final KidRegisterAuthFilter filter = new KidRegisterAuthFilter(
        Paths.KID_REGISTER, objectMapper);
    filter.setAuthenticationManager(authenticationManager());
    return filter;
  }

  @SuppressWarnings("RedundantThrows")
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    if (kidAuthenticationProvider != null) {
      auth.authenticationProvider(kidAuthenticationProvider);
    }
  }

  private OAuth2UserService<OAuth2UserRequest, OAuth2User> userService() {
    DefaultOAuth2UserService userService = new DefaultOAuth2UserService();

    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(
        objectMapper);
    messageConverter.setSupportedMediaTypes(Collections.singletonList(
        new MediaType("text", "javascript", StandardCharsets.UTF_8
        )));

    userService.setRestOperations(
        restTemplateBuilder
            .additionalMessageConverters(messageConverter)
            .build()
    );

    return userService;
  }

  private GrantedAuthoritiesMapper userAuthoritiesMapper() {
    return authorities -> authorities.stream()
        .map(authority -> {
          if (authority instanceof OAuth2UserAuthority) {
            return new OAuth2UserAuthority(
                Authorities.PARENT, ((OAuth2UserAuthority) authority).getAttributes());
          } else {
            return authority;
          }
        })
        .collect(Collectors.toList());
  }

}
