package me.itzg.kidsbank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.GenericConnectionStatusView;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.connect.GitHubConnectionFactory;

/**
 * @author Geoff Bourne
 * @since Nov 2017
 */
@Configuration
@ConditionalOnClass({SocialConfigurerAdapter.class, GitHubConnectionFactory.class})
@ConditionalOnProperty(prefix = "spring.social.github", name = "app-id")
@AutoConfigureBefore(SocialWebAutoConfiguration.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class GithubAutoConfiguration {

    @Configuration
    @EnableSocial
    @EnableConfigurationProperties(GithubProperties.class)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    protected class GithubSocialConfigurerAdapter extends SocialAutoConfigurerAdapter {

        private GithubProperties properties;

        @Autowired
        public GithubSocialConfigurerAdapter(GithubProperties properties) {
            this.properties = properties;
        }

        @Bean
        @ConditionalOnMissingBean(GitHub.class)
        @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
        public GitHub github(ConnectionRepository repository) {
            Connection<GitHub> connection = repository
                    .findPrimaryConnection(GitHub.class);
            return connection != null ? connection.getApi() : null;
        }

        @Bean(name = {"connect/githubConnect", "connect/githubConnected"})
        @ConditionalOnProperty(prefix = "spring.social", name = "auto-connection-views")
        public GenericConnectionStatusView facebookConnectView() {
            return new GenericConnectionStatusView("github", "GitHub");
        }

        @Override
        protected ConnectionFactory<?> createConnectionFactory() {
            return new GitHubConnectionFactory(properties.getAppId(), properties.getAppSecret());
        }
    }

}
