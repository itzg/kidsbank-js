package me.itzg.kidsbank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Configuration
public class GeneralConfig {
    private final KidsbankProperties properties;

    @Autowired
    public GeneralConfig(KidsbankProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Random rand() {
        return new Random();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(properties.getPasswordBcryptStrength());
    }

}
