package sk.codxa.cr.cashregisterservice.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.codxa.cr.cashregisterservice.model.CashInWrapper;
import sk.codxa.cr.cashregisterservice.model.entity.CashDeskEvent;
import sk.codxa.cr.cashregisterservice.exception.WrongInputException;
import sk.codxa.cr.cashregisterservice.model.BalanceWrapper;
import sk.codxa.cr.cashregisterservice.service.CashDeskService;

/**
 * Rest api for saving daily or interval balance.
 */
@RequestMapping(value = "/api")
@RestController
@Log4j
public class CashDeskController {

    private final CashDeskService cashDeskService;

    /**
     * Constructor.
     *
     * @param cashDeskService for manipulating with cashdesk event data.
     */
    @Autowired
    public CashDeskController(CashDeskService cashDeskService) {
        this.cashDeskService = cashDeskService;
    }

    @RequestMapping(value = "/recordBalance", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CashDeskEvent> recordDailyOrIntervalBalance(@RequestBody BalanceWrapper balanceWrapper) {
        if (balanceWrapper.getUserId() == null) {
            log.warn("User id is null.");
            throw new WrongInputException();
        }

        CashDeskEvent cashDeskEvent = cashDeskService.recordBalance(balanceWrapper);
        return new ResponseEntity<>(cashDeskEvent, HttpStatus.OK);
    }

    /**
     *
     * @param cashInWrapper for manipulating with cashIn event data
     * @return Response with HTTP Status
     */
    @RequestMapping(
            value = "/cashin",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CashDeskEvent> cashDeskInput(
            @RequestBody CashInWrapper cashInWrapper) {
        if (cashInWrapper.getUserId() != null && cashInWrapper.getBalance() != null) {
            CashDeskEvent cashDeskEvent = cashDeskService.insertMoney(cashInWrapper);
            return new ResponseEntity<>(cashDeskEvent, HttpStatus.OK);
        } else {
            throw new WrongInputException();
        }
    }
}