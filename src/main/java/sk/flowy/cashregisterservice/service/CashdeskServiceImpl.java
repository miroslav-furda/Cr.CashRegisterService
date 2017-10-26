package sk.flowy.cashregisterservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        CashdeskEvent cashdeskEvent = cashdeskEvents.get(cashdeskEvents.size() - 1);

        return cashdeskEvent.getEndOfShift() == null;
    }

    //TODO remove
    private void createShift(CashdeskUser cashdeskUser) {
        if (cashdeskUser.getCashdeskEvents()==null||cashdeskUser.getCashdeskEvents().isEmpty()){
            cashdeskUser.setCashdeskEvents(new ArrayList<>());
        }
        CashdeskEvent cashdeskEvent = new CashdeskEvent();
        cashdeskEvent.setStartOfShift(new Date(currentTimeMillis() - 24 * 60 * 60 * 1000));

        CashInEvent cashInEvent = new CashInEvent();
        cashInEvent.setBalance(50);
        cashInEvent.setCreatedAt(new Date(currentTimeMillis() - 24 * 60 * 60 * 1000));
        cashInEvent.setCashdeskEvent(cashdeskEvent);

        cashdeskEvent.setCashInEvents(Collections.singletonList(cashInEvent));
        cashdeskEvent.setCashdeskUser(cashdeskUser);

        cashdeskUser.getCashdeskEvents().add(cashdeskEvent);
        cashdeskEventRepository.save(cashdeskEvent);
    }

    @Override
    public CashdeskEvent doDailyBalance(Long userId, int cashBalance, int gastroTicketsBalance, int terminalBalance) {
        CashdeskUser cashdeskUser = cashdeskUserRepository.findOne(userId);
        createShift(cashdeskUser);

        List<CashdeskEvent> cashdeskEvents = cashdeskUser.getCashdeskEvents();
        CashdeskEvent cashdeskEvent = cashdeskEvents.get(cashdeskEvents.size() - 1);
        cashdeskEvent.setCashOutEvents(new ArrayList<>());

        CashOutEvent cashOutEvent = new CashOutEvent();
        cashOutEvent.setCashdeskEvent(cashdeskEvent);
        cashOutEvent.setCashBalance(cashBalance);
        cashOutEvent.setGastroTicketsBalance(gastroTicketsBalance);
        cashOutEvent.setTerminalBalance(terminalBalance);
        cashOutEvent.setCreatedAt(new Date());
        cashOutEvent.setDailyBalance(true);

        cashdeskEvent.getCashOutEvents().add(cashOutEvent);
        cashdeskEvent.setEndOfShift(new Date());

        cashdeskEventRepository.save(cashdeskEvent);

        return cashdeskEvent;
    }

    @Override
    public CashdeskEvent doIntervalBalance(Long userId, int cashBalance, int gastroTicketsBalance, int
            terminalBalance) {
        //TODO implement
        return null;
    }
}
