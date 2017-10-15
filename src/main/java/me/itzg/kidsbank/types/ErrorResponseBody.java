package me.itzg.kidsbank.types;

import lombok.Data;

import java.util.Collection;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Data
public class ErrorResponseBody {
    int status;
    String message;
    String error;
    Collection<? extends Object> errors;
}
