package sk.flowy.cashregisterservice.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.flowy.cashregisterservice.model.entity.CashInEvent;
import sk.flowy.cashregisterservice.model.entity.CashDeskEvent;
import sk.flowy.cashregisterservice.model.entity.CashDeskUser;
import sk.flowy.cashregisterservice.exception.CashDeskUserNotFoundException;
import sk.flowy.cashregisterservice.model.CashInWrapper;
import sk.flowy.cashregisterservice.repository.CashDeskEventRepository;
import sk.flowy.cashregisterservice.repository.CashDeskUserRepository;
import sk.flowy.cashregisterservice.exception.UserNotOnShiftException;
import sk.flowy.cashregisterservice.model.BalanceWrapper;
import sk.flowy.cashregisterservice.model.entity.CashOutEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


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
        Date createdAt = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        CashDeskEvent shift = getShift(cashInWrapper.getUserId());

        CashInEvent cashInEvent = new CashInEvent();
        cashInEvent.setBalance(cashInWrapper.getBalance());
        cashInEvent.setCreatedAt(createdAt);
        cashInEvent.setCashDeskEvent(shift);

        List<CashInEvent> cashInEvents = shift.getCashInEvents();
        cashInEvents.add(cashInEvent);

        return cashDeskEventRepository.save(shift);
    }

    private CashDeskEvent getShift(Long userId) {

        CashDeskUser cashdeskUser = cashDeskUserRepository.findOne(userId);
        if (cashdeskUser != null) {
            List<CashDeskEvent> cashdeskEvents = cashdeskUser.getCashDeskEvents();
            if (cashdeskEvents == null || cashdeskEvents.isEmpty()) {
                Date createdAt = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
                return createShift(userId, createdAt);
            } else {
                CashDeskEvent lastShift = cashdeskEvents.get(cashdeskEvents.size() - 1);
                if (lastShift.getEndOfShift() != null) {
                    Date createdAt = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
                    return createShift(userId, createdAt);
                } else {
                    return lastShift;
                }
            }
        } else {
            throw new CashDeskUserNotFoundException();
        }
    }

    private CashDeskEvent createShift(Long userId, Date startOfShift) {
        CashDeskEvent cashDeskEvent = new CashDeskEvent();
        CashDeskUser cashDeskUser = cashDeskUserRepository.findOne(userId);
        List<CashInEvent> cashInEvents = new ArrayList<>();

        cashDeskEvent.setCashDeskUser(cashDeskUser);
        cashDeskEvent.setStartOfShift(startOfShift);
        cashDeskEvent.setCashInEvents(cashInEvents);

        return cashDeskEvent;
    }

    @Override
    public CashDeskEvent recordBalance(BalanceWrapper balanceWrapper) {
        Long userId = balanceWrapper.getUserId();
        Optional<CashDeskUser> userOptional = getUserFromCurrentShift(userId);
        if (!userOptional.isPresent()) {
            log.warn("User " + userId + " has not began any shift yet.");
            throw new UserNotOnShiftException();
        }

        CashDeskUser cashDeskUser = userOptional.get();
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

    @Override
    public Optional<CashDeskUser> getUserFromCurrentShift(Long userId) {
        CashDeskUser cashDeskUser = cashDeskUserRepository.findOne(userId);

        if (cashDeskUser == null) {
            return Optional.empty();
        }

        List<CashDeskEvent> cashDeskEvents = cashDeskUser.getCashDeskEvents();
        if (cashDeskEvents == null || cashDeskEvents.isEmpty()) {
            return Optional.empty();
        }
        CashDeskEvent cashDeskEvent = cashDeskEvents.get(cashDeskEvents.size() - 1);

        if (cashDeskEvent.getStartOfShift() == null || cashDeskEvent.getEndOfShift() != null) {
            return Optional.empty();
        }

        return Optional.of(cashDeskUser);
    }
}
