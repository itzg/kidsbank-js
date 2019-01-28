package me.itzg.kidsbank.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/index.html");
        registry.addViewController("/parent/**").setViewName("/index.html");
        registry.addViewController("/kid/**").setViewName("/index.html");
    }
}
