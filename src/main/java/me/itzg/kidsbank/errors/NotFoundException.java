package me.itzg.kidsbank.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
