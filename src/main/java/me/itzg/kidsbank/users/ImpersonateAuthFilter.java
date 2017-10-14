package me.itzg.kidsbank.users;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public class ImpersonateAuthFilter extends OncePerRequestFilter {

    private final AntPathRequestMatcher pathMatcher;

    public ImpersonateAuthFilter(String defaultFilterProcessesUrl) {
        pathMatcher = new AntPathRequestMatcher(defaultFilterProcessesUrl);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (pathMatcher.matches(httpServletRequest)) {
            final String impersonateParentId = httpServletRequest.getHeader("X-Impersonate");
            final String authority = httpServletRequest.getHeader("X-Impersonate-Authority");

            if (impersonateParentId != null) {
                final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        impersonateParentId,
                        "",
                        Collections.singletonList(new SimpleGrantedAuthority(Optional.ofNullable(
                                authority).orElse(Authorities.PARENT))));

                SecurityContextHolder.getContext().setAuthentication(auth);

            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
