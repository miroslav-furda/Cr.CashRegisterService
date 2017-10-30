package sk.flowy.cashregisterservice.controller;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.flowy.cashregisterservice.exception.WrongInputException;
import sk.flowy.cashregisterservice.model.BalanceWrapper;
import sk.flowy.cashregisterservice.service.CashDeskService;
import sk.flowy.cashregisterservice.model.entity.CashDeskEvent;

import java.io.Serializable;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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

    @RequestMapping(value = "/recordBalance", method = POST, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CashDeskEvent> recordDailyOrIntervalBalance(@RequestBody BalanceWrapper balanceWrapper) {
        if (balanceWrapper.getUserId() == null) {
            log.warn("User id is null.");
            throw new WrongInputException();
        }

        CashDeskEvent cashDeskEvent = cashDeskService.recordBalance(balanceWrapper);
        return new ResponseEntity<>(cashDeskEvent, OK);
    }
}