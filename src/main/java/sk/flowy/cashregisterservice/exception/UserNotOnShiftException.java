package sk.flowy.cashregisterservice.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Exception thrown by api when request product cannot be found in database.
 */
@ResponseStatus(value = BAD_REQUEST, reason = "Sent user is not currently on shift.")
public class UserNotOnShiftException extends RuntimeException {
}
