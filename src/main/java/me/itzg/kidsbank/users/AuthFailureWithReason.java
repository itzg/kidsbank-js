package me.itzg.kidsbank.users;

import me.itzg.kidsbank.errors.BadCredentialFieldException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public class AuthFailureWithReason implements AuthenticationFailureHandler {

    public static final String REASON_TYPE = "X-Reason-Type";
    public static final String REASON = "X-Reason";
    public static final String REASON_FIELD = "X-Reason-Field";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.addHeader(REASON_TYPE, exception.getClass().getSimpleName());
        response.addHeader(REASON, exception.getMessage());
        if (exception instanceof BadCredentialFieldException) {
            final String field = ((BadCredentialFieldException) exception).getField();
            response.addHeader(REASON_FIELD, field);
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.getWriter().print(exception.getMessage());
        response.getWriter().flush();
    }
}
