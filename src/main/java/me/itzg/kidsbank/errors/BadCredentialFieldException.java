package me.itzg.kidsbank.errors;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public class BadCredentialFieldException extends AuthenticationException {
    private String field;

    public BadCredentialFieldException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
