package me.itzg.kidsbank.config;

import org.springframework.boot.autoconfigure.social.SocialProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Implemented here since Spring Boot does not yet provide auto config for Github Social.
 *
 * @author Geoff Bourne
 * @since Nov 2017
 */
@ConfigurationProperties("spring.social.github")
public class GithubProperties extends SocialProperties {
}
