package me.itzg.kidsbank.users;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private ObjectMapper objectMapper;

    public AuthFailureWithReason(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // It's required to set the status before writing to the body. Otherwise it'll force an OK status.
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.addHeader(REASON_TYPE, exception.getClass().getSimpleName());
        response.addHeader(REASON, exception.getMessage());
        if (exception instanceof BadCredentialFieldException) {
            objectMapper.writeValue(response.getWriter(),
                                    ((BadCredentialFieldException) exception).getFieldMessages());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }
        response.getWriter().flush();
    }
}
