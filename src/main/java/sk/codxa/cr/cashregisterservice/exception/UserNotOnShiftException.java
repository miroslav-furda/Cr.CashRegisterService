package sk.codxa.cr.cashregisterservice.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

/**
 * Exception thrown by api when user is not currently on shift.
 */
@ResponseStatus(value = METHOD_NOT_ALLOWED, reason = "Sent user is not currently on shift.")
public class UserNotOnShiftException extends RuntimeException {
}
