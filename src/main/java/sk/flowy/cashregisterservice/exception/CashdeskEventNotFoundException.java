package sk.flowy.cashregisterservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Problem getting CashdeskEvent object. Something bad happened on server!")
public class CashdeskEventNotFoundException extends RuntimeException {
}
