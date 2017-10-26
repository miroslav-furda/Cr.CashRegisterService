package sk.flowy.cashregisterservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.flowy.cashregisterservice.service.CashdeskService;
import sk.flowy.cashregisterservice.model.BalanceWrapper;
import sk.flowy.cashregisterservice.model.entity.CashdeskEvent;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping(value = "/api")
@RestController
public class CashdeskController {

    private final CashdeskService cashdeskService;

    @Autowired
    public CashdeskController(CashdeskService cashdeskService) {
        this.cashdeskService = cashdeskService;
    }

    @RequestMapping(value = "/dailyBalance", method = POST, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CashdeskEvent> createDailyBalance(@RequestBody BalanceWrapper balanceWrapper) {
        /*if (!cashdeskService.isUserOnShift(balanceWrapper.getUserId())) {
            throw new UserNotOnShiftException();
        }*/

        CashdeskEvent cashDeskEvent = cashdeskService.doDailyBalance(balanceWrapper.getUserId(), balanceWrapper
                .getCashBalance(), balanceWrapper
                .getGastroTicketsBalance(), balanceWrapper.getTerminalBalance());

        return new ResponseEntity<>(cashDeskEvent, OK);
    }
}
