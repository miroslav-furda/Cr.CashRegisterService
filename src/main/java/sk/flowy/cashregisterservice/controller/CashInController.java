package sk.flowy.cashregisterservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.flowy.cashregisterservice.exception.LackOfInformationForCashInException;
import sk.flowy.cashregisterservice.model.CashInWrapper;
import sk.flowy.cashregisterservice.service.CashDeskService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/flowy")
public class CashInController {

    private final CashDeskService cashDeskService;

    @Autowired
    public CashInController(CashDeskService cashDeskService) {
        this.cashDeskService = cashDeskService;
    }

    @RequestMapping(
            value = "/cashin",
            method = RequestMethod.POST,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity cashDeskInput(
            @RequestBody CashInWrapper cashInWrapper) {
        if (cashInWrapper.getUserId() != null && cashInWrapper.getBalance() != null) {
            if (cashDeskService.insertMoney(cashInWrapper) != null) {
                return new ResponseEntity(HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new LackOfInformationForCashInException();
        }
    }
}
