package sk.flowy.cashregisterservice.exception;


import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Exception thrown by client wrong input was sent.
 */
@ResponseStatus(value = BAD_REQUEST, reason = "Wrong input! Missing user id.")
public class WrongInputException extends RuntimeException {
}
