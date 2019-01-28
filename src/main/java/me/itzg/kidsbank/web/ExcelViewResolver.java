package me.itzg.kidsbank.web;

import java.util.Locale;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * @author Geoff Bourne
 * @since Jan 2019
 */
@Component
public class ExcelViewResolver implements ViewResolver {

  @Override
  public View resolveViewName(String viewName, Locale locale) throws Exception {
    return new ExcelView();
  }
}
