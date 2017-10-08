package me.itzg.kidsbank.web;

import me.itzg.kidsbank.types.Profile;
import me.itzg.kidsbank.types.Role;
import me.itzg.kidsbank.users.Authorities;
import me.itzg.kidsbank.users.KidAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@RestController
@RequestMapping(Paths.API)
public class CommonApi {

    @GetMapping(Paths.CURRENT_USER)
    public ResponseEntity<Profile> currentUser(Principal principal) {
        if (principal instanceof SocialAuthenticationToken) {
            final SocialAuthenticationToken token = (SocialAuthenticationToken) principal;
            final Profile profile = new Profile();

            profile.setUserId(token.getName());
            profile.setDisplayName(token.getConnection().getDisplayName());
            profile.setProfileImageUrl(token.getConnection().getImageUrl());
            if (token.getAuthorities().contains(Authorities.PARENT_AUTHORITY)) {
                profile.setRole(Role.PARENT);
            } else {
                throw new IllegalStateException("Social login is in use, but wrong authority");
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
}
