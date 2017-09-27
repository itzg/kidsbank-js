package me.itzg.kidsbank.config;

import me.itzg.kidsbank.users.Roles;
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
                     @SuppressWarnings("SpringJavaAutowiringInspection")
                             SocialAuthenticationServiceLocator socialAuthenticationServiceLocator,
                     SocialUserDetailsService userAccountService) {
        this.userIdSource = userIdSource;
        this.usersConnectionRepository = usersConnectionRepository;
        this.socialAuthenticationServiceLocator = socialAuthenticationServiceLocator;
        this.userAccountService = userAccountService;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/")
                .antMatchers("/index.html")
                .antMatchers("/js/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/parents/**").hasAuthority(Roles.PARENT)
                .antMatchers("/api/kids/**").hasAuthority(Roles.KID)
                .antMatchers("/api/currentUser").permitAll()
                .antMatchers("/signin/**").permitAll()
                .antMatchers("/connect/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(socialAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                .logout().deleteCookies("JSESSIONID").logoutUrl("/signout").logoutSuccessUrl("/")
        ;
    }

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
        filter.setFilterProcessesUrl("/signin");
        filter.setSignupUrl(null);
        filter.setConnectionAddedRedirectUrl("/#/myAccount");
        filter.setPostLoginUrl("/#/myAccount"); //always open account profile page after login
//        filter.setRememberMeServices(rememberMeServices());
        return filter;
    }

    @Bean
    public SocialAuthenticationProvider socialAuthenticationProvider() {
        return new SocialAuthenticationProvider(usersConnectionRepository, userAccountService);
    }

}
