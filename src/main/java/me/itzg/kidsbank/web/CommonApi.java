package me.itzg.kidsbank.web;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import me.itzg.kidsbank.types.ParentUserDetails;
import me.itzg.kidsbank.types.Profile;
import me.itzg.kidsbank.types.Role;
import me.itzg.kidsbank.users.Authorities;
import me.itzg.kidsbank.users.KidAuthenticationToken;
import me.itzg.kidsbank.users.OAuth2UserProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@RestController
@RequestMapping(Paths.API)
@Slf4j
public class CommonApi {

    private final OAuth2UserProfiler oAuth2Profiler;

    @Autowired
    public CommonApi(OAuth2UserProfiler oAuth2Profiler) {
        this.oAuth2Profiler = oAuth2Profiler;
    }

    @GetMapping(Paths.CURRENT_USER)
    public ResponseEntity<Profile> currentUser(Principal principal,
                                               HttpServletRequest request) {
        if (principal instanceof OAuth2AuthenticationToken) {
            final OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            final Profile profile = new Profile();

            final Object details = token.getDetails();
            if (details instanceof ParentUserDetails) {
                profile.setUserId(((ParentUserDetails) details).getId());
            }
            else {
                throw new IllegalStateException(
                    "Expected auth token details to be a ParentUserDetails");
            }


            profile.setDisplayName(oAuth2Profiler.getDisplayName(token));
//            profile.setProfileImageUrl(token.getConnection().getImageUrl());
            if (token.getAuthorities().stream().anyMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals(Authorities.PARENT))) {
                profile.setRole(Role.PARENT);
            } else {
                throw new IllegalStateException("OAuth2 login is in use, but wrong authority");
            }

            return ResponseEntity.ok(profile);
        } else if (principal instanceof KidAuthenticationToken) {
            final KidAuthenticationToken token = (KidAuthenticationToken) principal;
            final Profile profile = new Profile();

            profile.setUserId(token.getName());
            profile.setDisplayName(token.getName());
            if (token.getAuthorities().contains(Authorities.KID_AUTHORITY)) {
                profile.setRole(Role.KID);
            } else {
                throw new IllegalStateException("Kid login is in use, but wrong authority");
            }

            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    private void forceLogout(HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }
}
