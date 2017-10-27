package sk.flowy.cashregisterservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Request is missing some important information for providing service")
public class LackOfInformationForCashInException extends RuntimeException {
}
