package me.itzg.kidsbank.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@RestController
@RequestMapping("/api")
public class CommonApi {

    @GetMapping("currentUser")
    public ResponseEntity<Principal> currentUser(Principal principal) {
        if (principal != null) {
            return ResponseEntity.ok(principal);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}
