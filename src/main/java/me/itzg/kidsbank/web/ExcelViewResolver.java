package me.itzg.kidsbank.web;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Component
public class ExcelViewResolver implements ViewResolver {
    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        return new ExcelView();
    }
}
