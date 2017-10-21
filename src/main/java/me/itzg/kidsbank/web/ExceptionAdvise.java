package me.itzg.kidsbank.web;

import me.itzg.kidsbank.types.ErrorResponseBody;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@ControllerAdvice
public class ExceptionAdvise {

    @ExceptionHandler
    public ResponseEntity<ErrorResponseBody> handleIllegalArgument(IllegalArgumentException e) {
        return handleBadRequest("Bad request", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseBody> handleIllegalArgument(InvalidFormatException e) {
        return handleBadRequest("Invalid file format", e.getMessage());
    }

    private ResponseEntity<ErrorResponseBody> handleBadRequest(String error, String message) {
        final ErrorResponseBody body = new ErrorResponseBody();
        body.setError(error);
        body.setMessage(message);
        body.setStatus(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.badRequest().body(body);
    }

}
