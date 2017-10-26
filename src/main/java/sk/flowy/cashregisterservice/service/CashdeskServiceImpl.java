package sk.flowy.cashregisterservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.flowy.cashregisterservice.exception.UserNotOnShiftException;
import sk.flowy.cashregisterservice.model.entity.CashInEvent;
import sk.flowy.cashregisterservice.model.entity.CashOutEvent;
import sk.flowy.cashregisterservice.model.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.model.entity.CashdeskUser;
import sk.flowy.cashregisterservice.repository.CashdeskEventRepository;
import sk.flowy.cashregisterservice.repository.CashdeskUserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.lang.System.currentTimeMillis;

@Service
public class CashdeskServiceImpl implements CashdeskService {

    private final CashdeskUserRepository cashdeskUserRepository;
    private final CashdeskEventRepository cashdeskEventRepository;

    @Autowired
    public CashdeskServiceImpl(CashdeskUserRepository cashdeskUserRepository, CashdeskEventRepository
            cashdeskEventRepository) {
        this.cashdeskUserRepository = cashdeskUserRepository;
        this.cashdeskEventRepository = cashdeskEventRepository;
    }

    @Override
    public boolean isUserOnShift(Long userId) {
        CashdeskUser cashdeskUser = cashdeskUserRepository.findOne(userId);

        List<CashdeskEvent> cashdeskEvents = cashdeskUser.getCashdeskEvents();
        if (cashdeskEvents.isEmpty()) {
            return false;
        }
        //take last shift
        CashdeskEvent cashdeskEvent = cashdeskEvents.get(cashdeskEvents.size() - 1);

        return cashdeskEvent.getStartOfShift()!=null && cashdeskEvent.getEndOfShift() == null;
    }

    @Override
    public CashdeskEvent doDailyBalance(Long userId, int cashBalance, int gastroTicketsBalance, int terminalBalance) {
        if (!isUserOnShift(userId)) {
            throw new UserNotOnShiftException();
        }
        // smena zacala -> cashdeskEvents ma v sebe aspon jeden event -> cashInEvent

        CashdeskUser cashdeskUser = cashdeskUserRepository.findOne(userId);
        CashdeskEvent cashdeskEvent = cashdeskUser.getCashdeskEvents().get(cashdeskUser.getCashdeskEvents().size() - 1);

        //inicializujeme cash out eventy
        if (cashdeskEvent.getCashOutEvents() == null) {
            cashdeskEvent.setCashOutEvents(new ArrayList<>());
        }

        cashdeskEvent.setEndOfShift(new Date());

        CashOutEvent cashOutEvent = new CashOutEvent();
        cashOutEvent.setCashBalance(cashBalance);
        cashOutEvent.setGastroTicketsBalance(gastroTicketsBalance);
        cashOutEvent.setTerminalBalance(terminalBalance);
        cashOutEvent.setCreatedAt(new Date());
        cashOutEvent.setDailyBalance(true);
        cashOutEvent.setCashdeskEvent(cashdeskEvent);

        cashdeskEvent.getCashOutEvents().add(cashOutEvent);

        return cashdeskEventRepository.save(cashdeskEvent);
    }

    @Override
    public CashdeskEvent doIntervalBalance(Long userId, int cashBalance, int gastroTicketsBalance, int
            terminalBalance) {
        //TODO implement
        return null;
    }

    //TODO remove
    private CashdeskEvent createShift(Long userId) {
        CashdeskUser cashdeskUser = cashdeskUserRepository.findOne(userId);

        if (cashdeskUser.getCashdeskEvents() == null || cashdeskUser.getCashdeskEvents().isEmpty()) {
            cashdeskUser.setCashdeskEvents(new ArrayList<>());
        }

        CashdeskEvent cashdeskEvent = new CashdeskEvent();
        cashdeskEvent.setStartOfShift(new Date(currentTimeMillis() - 24 * 60 * 60 * 1000));

        CashInEvent cashInEvent = new CashInEvent();
        cashInEvent.setBalance(50);
        cashInEvent.setCreatedAt(new Date(currentTimeMillis() - 24 * 60 * 60 * 1000));
        cashInEvent.setCashdeskEvent(cashdeskEvent);

        List<CashInEvent> cashInEvents = cashdeskEvent.getCashInEvents();
        if (cashInEvents == null || cashInEvents.isEmpty()) {
            ArrayList<CashInEvent> events = new ArrayList<>();
            events.add(cashInEvent);
            cashdeskEvent.setCashInEvents(events);
        } else {
            cashdeskEvent.getCashInEvents().add(cashInEvent);
        }

        cashdeskEvent.setCashdeskUser(cashdeskUser);

        cashdeskUser.getCashdeskEvents().add(cashdeskEvent);

        return cashdeskEventRepository.save(cashdeskEvent);
    }
}
