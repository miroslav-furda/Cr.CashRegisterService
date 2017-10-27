package sk.flowy.cashregisterservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Cashdesk User not found in DB!")
public class CashdeskUserNotFoundException extends RuntimeException {
}
