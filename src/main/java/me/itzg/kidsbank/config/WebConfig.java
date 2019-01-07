package me.itzg.kidsbank.config;

import me.itzg.kidsbank.web.ExcelView;
import me.itzg.kidsbank.web.Slf4jRequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/parent/**").setViewName("/index.html");
        registry.addViewController("/kid/**").setViewName("/index.html");
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.enableContentNegotiation(
            new ExcelView()
        );
    }

    @Bean
    public Slf4jRequestLoggingFilter loggingFilter() {
        final Slf4jRequestLoggingFilter filter = new Slf4jRequestLoggingFilter();
        filter.setIncludeHeaders(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(100);
        filter.setBeforeMessagePrefix("BEFORE : ");
        filter.setAfterMessagePrefix("AFTER : ");
        return filter;
    }
}
