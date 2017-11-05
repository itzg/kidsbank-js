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
    @SuppressWarnings("RedundantThrows")
    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        if (viewName.toLowerCase().endsWith(ContentTypes.XLSX_EXTENSION)) {
            return new ExcelView();
        } else {
            return null;
        }
    }
}
