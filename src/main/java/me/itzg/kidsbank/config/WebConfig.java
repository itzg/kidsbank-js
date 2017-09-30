package me.itzg.kidsbank.config;

import me.itzg.kidsbank.users.Authorities;
import me.itzg.kidsbank.web.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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
public class WebConfig extends WebSecurityConfigurerAdapter {

    private final SocialUserDetailsService userAccountService;

    private final UserIdSource userIdSource;

    private final UsersConnectionRepository usersConnectionRepository;

    private final SocialAuthenticationServiceLocator socialAuthenticationServiceLocator;

    @Autowired
    public WebConfig(UserIdSource userIdSource,
                     UsersConnectionRepository usersConnectionRepository,
                     @SuppressWarnings({"SpringJavaAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection"})
                             SocialAuthenticationServiceLocator socialAuthenticationServiceLocator,
                     SocialUserDetailsService userAccountService) {
        this.userIdSource = userIdSource;
        this.usersConnectionRepository = usersConnectionRepository;
        this.socialAuthenticationServiceLocator = socialAuthenticationServiceLocator;
        this.userAccountService = userAccountService;
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
                .logout().deleteCookies("JSESSIONID").logoutUrl(Paths.SIGNOUT).logoutSuccessUrl(Paths.ROOT)
        ;
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(socialAuthenticationProvider());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
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
//        filter.setRememberMeServices(rememberMeServices());
        return filter;
    }

    @Bean
    public SocialAuthenticationProvider socialAuthenticationProvider() {
        return new SocialAuthenticationProvider(usersConnectionRepository, userAccountService);
    }

}
