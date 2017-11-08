package sk.codxa.cr.cashregisterservice.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.codxa.cr.cashregisterservice.exception.CashDeskUserNotFoundException;
import sk.codxa.cr.cashregisterservice.exception.UserNotOnShiftException;
import sk.codxa.cr.cashregisterservice.model.CashInWrapper;
import sk.codxa.cr.cashregisterservice.model.entity.CashDeskEvent;
import sk.codxa.cr.cashregisterservice.model.entity.CashOutEvent;
import sk.codxa.cr.cashregisterservice.repository.CashDeskEventRepository;
import sk.codxa.cr.cashregisterservice.repository.CashDeskUserRepository;
import sk.codxa.cr.cashregisterservice.model.entity.CashInEvent;
import sk.codxa.cr.cashregisterservice.model.entity.CashDeskUser;
import sk.codxa.cr.cashregisterservice.model.BalanceWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Log4j
@Service
public class CashDeskServiceImpl implements CashDeskService {

    private final CashDeskEventRepository cashDeskEventRepository;

    private final CashDeskUserRepository cashDeskUserRepository;

    @Autowired
    public CashDeskServiceImpl(CashDeskEventRepository cashDeskEventRepository, CashDeskUserRepository cashDeskUserRepository) {
        this.cashDeskEventRepository = cashDeskEventRepository;
        this.cashDeskUserRepository = cashDeskUserRepository;
    }

    @Override
    public CashDeskEvent insertMoney(CashInWrapper cashInWrapper) {
        Date createdAt = new Date();
        CashDeskUser cashDeskUser = getUser(cashInWrapper.getUserId());
        CashDeskEvent cashDeskEvent;
        if (!isUserOnShift(cashDeskUser)) {
            cashDeskEvent = createShift(cashDeskUser);
        } else {
            List<CashDeskEvent> cashDeskEvents = cashDeskUser.getCashDeskEvents();
            cashDeskEvent = cashDeskEvents.get(cashDeskEvents.size() - 1);
        }


        CashInEvent cashInEvent = new CashInEvent();
        cashInEvent.setBalance(cashInWrapper.getBalance());
        cashInEvent.setCreatedAt(createdAt);
        cashInEvent.setCashDeskEvent(cashDeskEvent);

        List<CashInEvent> cashInEvents = cashDeskEvent.getCashInEvents();
        cashInEvents.add(cashInEvent);

        return cashDeskEventRepository.save(cashDeskEvent);
    }

    private CashDeskUser getUser(Long userId) {
        CashDeskUser cashdeskUser = cashDeskUserRepository.findOne(userId);
        if (cashdeskUser == null) {
            throw new CashDeskUserNotFoundException();
        }
        if (cashdeskUser.getCashDeskEvents() == null) {
            cashdeskUser.setCashDeskEvents(new ArrayList<CashDeskEvent>());
        }
        return cashdeskUser;

    }

    @Override
    public CashDeskEvent recordBalance(BalanceWrapper balanceWrapper) {
        Long userId = balanceWrapper.getUserId();
        CashDeskUser cashDeskUser = getUser(userId);
        if (!isUserOnShift(cashDeskUser)) {
            log.warn("User " + userId + " has not began any shift yet.");
            throw new UserNotOnShiftException();
        }

        CashDeskEvent cashDeskEvent = cashDeskUser.getCashDeskEvents().get(cashDeskUser.getCashDeskEvents().size() - 1);

        if (cashDeskEvent.getCashOutEvents() == null) {
            cashDeskEvent.setCashOutEvents(new ArrayList<>());
        }

        CashOutEvent cashOutEvent = new CashOutEvent();
        cashOutEvent.setCashBalance(balanceWrapper.getCashBalance());
        cashOutEvent.setGastroTicketsBalance(balanceWrapper.getGastroTicketsBalance());
        cashOutEvent.setTerminalBalance(balanceWrapper.getTerminalBalance());
        cashOutEvent.setCreatedAt(new Date());
        cashOutEvent.setCashDeskEvent(cashDeskEvent);

        if (balanceWrapper.isEndOfShift()) {
            cashDeskEvent.setEndOfShift(new Date());
            cashOutEvent.setDailyBalance(true);
        } else {
            cashOutEvent.setDailyBalance(false);
        }

        cashDeskEvent.getCashOutEvents().add(cashOutEvent);
        return cashDeskEventRepository.save(cashDeskEvent);
    }

    private boolean isUserOnShift(CashDeskUser cashDeskUser) {
        List<CashDeskEvent> cashdeskEvents = cashDeskUser.getCashDeskEvents();
        if (!cashdeskEvents.isEmpty() && (cashdeskEvents.get(cashdeskEvents.size() - 1).getEndOfShift() == null)){
            return true;
        }
        return false;
    }

    private CashDeskEvent createShift(CashDeskUser cashDeskUser) {
        CashDeskEvent cashDeskEvent = new CashDeskEvent();
        List<CashInEvent> cashInEvents = new ArrayList<>();

        cashDeskEvent.setCashDeskUser(cashDeskUser);
        Date startOfShift = new Date();
        cashDeskEvent.setStartOfShift(startOfShift);
        cashDeskEvent.setCashInEvents(cashInEvents);

        return cashDeskEvent;
    }

}
