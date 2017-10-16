package me.itzg.kidsbank.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.itzg.kidsbank.errors.BadCredentialFieldException;
import me.itzg.kidsbank.types.ErrorResponseBody;
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

    private ObjectMapper objectMapper;

    public AuthFailureWithReason(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        final ErrorResponseBody error = new ErrorResponseBody();
        error.setMessage(exception.getMessage());
        error.setError(exception.getClass().getSimpleName());

        final int status;
        if (exception instanceof BadCredentialFieldException) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
            error.setErrors(
                    ((BadCredentialFieldException) exception).getFieldMessages().entrySet()
            );
        } else {
            status = HttpServletResponse.SC_FORBIDDEN;
        }
        error.setStatus(status);
        response.setStatus(status);

        // NOTE: It's required to set the status before writing to the body. Otherwise it'll force an OK status.
        objectMapper.writeValue(response.getWriter(), error);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().flush();
    }
}
