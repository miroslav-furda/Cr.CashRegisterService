package sk.flowy.cashregisterservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.flowy.cashregisterservice.entity.CashInEvent;
import sk.flowy.cashregisterservice.entity.CashOutEvent;
import sk.flowy.cashregisterservice.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.entity.CashdeskUser;
import sk.flowy.cashregisterservice.repository.CashdeskEventRepository;
import sk.flowy.cashregisterservice.repository.CashdeskUserRepository;

import java.util.Date;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * TODO remove controller -> servers only as an example working with entities.
 */
@RestController
@RequestMapping()
public class TempController {

    @Autowired
    private CashdeskEventRepository cashdeskEventRepository;

    @Autowired
    private CashdeskUserRepository cashdeskUserRepository;

    @RequestMapping(value = "/test/{userId}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CashdeskUser> test(@PathVariable("userId") Long ean) {
        Date startOfShift = new Date(System.currentTimeMillis()-24*60*60*1000);
        Date endOfShift = new Date();

        CashInEvent cashInEvent = new CashInEvent();
        cashInEvent.setBalance(100);
        cashInEvent.setCreatedAt(startOfShift);

        CashOutEvent cashOutEvent = new CashOutEvent();
        cashOutEvent.setCashBalance(200);
        cashOutEvent.setCreatedAt(endOfShift);

        CashdeskEvent cashdeskEvent = new CashdeskEvent();
        cashdeskEvent.setCashInEvent(cashInEvent);
        cashdeskEvent.setCashOutEvent(cashOutEvent);
        cashdeskEvent.setStartOfShift(startOfShift);
        cashdeskEvent.setEndOfShift(endOfShift);

        cashInEvent.setCashdeskEvent(cashdeskEvent);
        cashOutEvent.setCashdeskEvent(cashdeskEvent);

        CashdeskUser cashdeskUser = cashdeskUserRepository.findOne(ean);

        cashdeskEvent.setCashdeskUser(cashdeskUser);
        cashdeskEventRepository.save(cashdeskEvent);

        return new ResponseEntity<>(cashdeskEvent.getCashdeskUser(), OK);
    }
}
