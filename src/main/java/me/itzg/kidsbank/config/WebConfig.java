package me.itzg.kidsbank.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.itzg.kidsbank.users.Authorities;
import me.itzg.kidsbank.users.ImpersonateAuthFilter;
import me.itzg.kidsbank.users.KidAuthenticationProvider;
import me.itzg.kidsbank.users.KidLoginAuthFilter;
import me.itzg.kidsbank.users.KidRegisterAuthFilter;
import me.itzg.kidsbank.web.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SocialAuthenticationProvider;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.social.security.SocialUserDetailsService;

import static java.lang.String.format;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebConfig extends WebSecurityConfigurerAdapter {

    private final SocialUserDetailsService userAccountService;
    private final ObjectMapper objectMapper;
    private final KidAuthenticationProvider kidAuthenticationProvider;
    private Environment env;

    private final UserIdSource userIdSource;

    private final UsersConnectionRepository usersConnectionRepository;

    private final SocialAuthenticationServiceLocator socialAuthenticationServiceLocator;

    @Autowired
    public WebConfig(UserIdSource userIdSource,
                     UsersConnectionRepository usersConnectionRepository,
                     @SuppressWarnings({"SpringJavaAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection"})
                             SocialAuthenticationServiceLocator socialAuthenticationServiceLocator,
                     SocialUserDetailsService userAccountService,
                     ObjectMapper objectMapper,
                     KidAuthenticationProvider kidAuthenticationProvider,
                     Environment env) {
        this.userIdSource = userIdSource;
        this.usersConnectionRepository = usersConnectionRepository;
        this.socialAuthenticationServiceLocator = socialAuthenticationServiceLocator;
        this.userAccountService = userAccountService;
        this.objectMapper = objectMapper;
        this.kidAuthenticationProvider = kidAuthenticationProvider;
        this.env = env;
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(Paths.ROOT)
                .antMatchers("/index.html")
                .antMatchers("/js/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(format("%s/**", Paths.API_PARENT)).hasAuthority(Authorities.PARENT)
                .antMatchers(format("%s/**", Paths.API_KID)).hasAuthority(Authorities.KID)
                .antMatchers(format("%s/%s", Paths.API, Paths.CURRENT_USER)).permitAll()
                .antMatchers(format("%s/**", Paths.SIGNIN)).permitAll()
                .antMatchers("/connect/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(socialAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterBefore(kidRegistrationFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterBefore(kidLoginFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                .logout().deleteCookies("JSESSIONID").logoutUrl(Paths.SIGNOUT).logoutSuccessUrl(Paths.ROOT)
        ;

        if (env.acceptsProfiles(Profiles.IMPERSONATE)) {
            http.addFilterBefore(new ImpersonateAuthFilter("/api/**"),
                                 SocialAuthenticationFilter.class);
        }
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    private KidLoginAuthFilter kidLoginFilter() throws Exception {
        final KidLoginAuthFilter filter = new KidLoginAuthFilter(Paths.KID_LOGIN, objectMapper);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    private KidRegisterAuthFilter kidRegistrationFilter() throws Exception {
        final KidRegisterAuthFilter filter = new KidRegisterAuthFilter(Paths.KID_REGISTER, objectMapper);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(socialAuthenticationProvider());
        auth.authenticationProvider(kidAuthenticationProvider);
    }

    @Bean
    public SocialAuthenticationFilter socialAuthenticationFilter() throws Exception {
        SocialAuthenticationFilter filter = new SocialAuthenticationFilter(
                authenticationManager(), userIdSource,
                usersConnectionRepository, socialAuthenticationServiceLocator);
        filter.setFilterProcessesUrl(Paths.SIGNIN);
        filter.setSignupUrl(null);
        filter.setConnectionAddedRedirectUrl(Paths.PARENT);
        filter.setPostLoginUrl(Paths.PARENT);
        // ...otherwise it redirect back to the last 403'ed API call
        filter.setAlwaysUsePostLoginUrl(true);
        return filter;
    }

    @Bean
    public SocialAuthenticationProvider socialAuthenticationProvider() {
        return new SocialAuthenticationProvider(usersConnectionRepository, userAccountService);
    }

}
