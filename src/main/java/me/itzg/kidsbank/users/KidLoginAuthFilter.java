package me.itzg.kidsbank.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.itzg.kidsbank.types.KidLogin;
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
public class KidLoginAuthFilter extends AbstractAuthenticationProcessingFilter {

    private ObjectMapper objectMapper;

    public KidLoginAuthFilter(String defaultFilterProcessesUrl, ObjectMapper objectMapper) {
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

        final KidLogin kidLogin = objectMapper.readValue(request.getInputStream(), KidLogin.class);

        final KidAuthenticationToken authToken = new KidAuthenticationToken(kidLogin);

        return getAuthenticationManager().authenticate(authToken);
    }

}
