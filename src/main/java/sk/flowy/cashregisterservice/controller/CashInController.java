package sk.flowy.cashregisterservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<Long> cashDeskInput(
            @RequestBody CashInWrapper cashInWrapper) {
        Long cashdeskEventId = 0L;
        if (cashInWrapper.getUserId() != null && cashInWrapper.getBalance() != null) {
            cashdeskEventId = cashdeskService.insertMoney(cashInWrapper.getUserId(), cashInWrapper.getBalance(), cashInWrapper.getCashdeskEventId());
        }

        if (cashdeskEventId != 0L) {
            return new ResponseEntity<>(cashdeskEventId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(cashdeskEventId, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
