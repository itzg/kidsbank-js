package me.itzg.kidsbank.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.itzg.kidsbank.types.KidRegistration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public class KidRegisterAuthFilter extends AbstractAuthenticationProcessingFilter {

    private ObjectMapper objectMapper;

    public KidRegisterAuthFilter(String defaultFilterProcessesUrl, ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl, HttpMethod.POST.toString()));
        setAuthenticationSuccessHandler(new OkSuccessHandler());
        setAuthenticationFailureHandler(new AuthFailureWithReason(objectMapper));
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            return null;
        }

        final KidRegistration kidRegistration = objectMapper.readValue(request.getInputStream(), KidRegistration.class);

        final KidAuthenticationToken authToken = new KidAuthenticationToken(kidRegistration);

        return getAuthenticationManager().authenticate(authToken);
    }

}
