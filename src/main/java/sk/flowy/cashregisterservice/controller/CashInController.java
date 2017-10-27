package sk.flowy.cashregisterservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.flowy.cashregisterservice.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.exception.LackOfInformationForCashInException;
import sk.flowy.cashregisterservice.model.CashInWrapper;
import sk.flowy.cashregisterservice.service.CashdeskService;

@RestController
@RequestMapping("/api/flowy")
public class CashInController {

    private final CashdeskService cashdeskService;

    @Autowired
    public CashInController(CashdeskService cashdeskService) {
        this.cashdeskService = cashdeskService;
    }

    @RequestMapping(
            value = "/cashin",
            method = RequestMethod.POST)
    public ResponseEntity<CashdeskEvent> cashDeskInput(
            @RequestBody CashInWrapper cashInWrapper) {
        CashdeskEvent cashdeskEvent = null;
        if (cashInWrapper.getUserId() != null && cashInWrapper.getBalance() != null) {
            cashdeskEvent = cashdeskService.insertMoney(cashInWrapper);
        } else {
            throw new LackOfInformationForCashInException();
        }

        if (cashdeskEvent != null) {
            return new ResponseEntity<>(cashdeskEvent, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(cashdeskEvent, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
