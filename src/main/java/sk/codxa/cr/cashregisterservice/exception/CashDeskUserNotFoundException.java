package sk.codxa.cr.cashregisterservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Cash Desk User not found in DB!")
public class CashDeskUserNotFoundException extends RuntimeException {
}
