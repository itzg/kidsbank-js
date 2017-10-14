package me.itzg.kidsbank.errors;

import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
public class BadCredentialFieldException extends AuthenticationException {
    private Map<String, String> fieldMessages = new HashMap<>();

    public BadCredentialFieldException(String message) {
        super(message);
    }

    public BadCredentialFieldException(String message, String field, String fieldMessage) {
        super(message);
        addFieldMessage(field, fieldMessage);
    }

    public void addFieldMessage(String field, String message) {
        fieldMessages.put(field, message);
    }

    public Map<String, String> getFieldMessages() {
        return fieldMessages;
    }
}
