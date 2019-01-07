package me.itzg.kidsbank.web;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

/**
 * @author Geoff Bourne
 * @since Mar 2018
 */
@Slf4j
public class Slf4jRequestLoggingFilter extends AbstractRequestLoggingFilter implements Ordered {
    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return log.isTraceEnabled();
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        log.trace(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        log.trace(message);
    }

    @Override
    public int getOrder() {
        // use magic offset like other filters to place this before security filter chain
        return OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER - 104;
    }
}
