package me.itzg.kidsbank.config;

import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.web.ExcelView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/index.html");
        registry.addViewController("/parent/**").setViewName("/index.html");
        registry.addViewController("/kid/**").setViewName("/index.html");
    }

    @Bean
    public ViewResolver excelViewResolver() {
        log.info("Registering Excel view resolver");
        return (viewName, locale) -> new ExcelView();
    }
}
