package sk.flowy.cashregisterservice.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.flowy.cashregisterservice.exception.UserNotOnShiftException;
import sk.flowy.cashregisterservice.model.entity.CashDeskEvent;
import sk.flowy.cashregisterservice.model.entity.CashDeskUser;
import sk.flowy.cashregisterservice.model.entity.CashOutEvent;
import sk.flowy.cashregisterservice.repository.CashDeskEventRepository;
import sk.flowy.cashregisterservice.repository.CashDeskUserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Default {@link CashDeskService} implementation.
 */
@Service
@Log4j
public class CashDeskServiceImpl implements CashDeskService {

    private final CashDeskUserRepository cashDeskUserRepository;
    private final CashDeskEventRepository cashDeskEventRepository;

    /**
     * Constructor.
     *
     * @param cashDeskUserRepository repository for amnipulating with CashDeskUser table.
     * @param cashDeskEventRepository repository for amnipulating with CashDeskEvent table.
     */
    @Autowired
    public CashDeskServiceImpl(CashDeskUserRepository cashDeskUserRepository, CashDeskEventRepository
            cashDeskEventRepository) {
        this.cashDeskUserRepository = cashDeskUserRepository;
        this.cashDeskEventRepository = cashDeskEventRepository;
    }


    @Override
    public CashDeskEvent recordBalance(Long userId, int cashBalance, int gastroTicketsBalance, int terminalBalance,
                                       boolean endOfShift) {
        if (!isUserOnShift(userId)) {
            log.warn("User " + userId + " has not began any shift yet.");
            throw new UserNotOnShiftException();
        }
        CashDeskUser cashDeskUser = cashDeskUserRepository.findOne(userId);
        CashDeskEvent cashDeskEvent = cashDeskUser.getCashDeskEvents().get(cashDeskUser.getCashDeskEvents().size() - 1);

        if (cashDeskEvent.getCashOutEvents() == null) {
            cashDeskEvent.setCashOutEvents(new ArrayList<>());
        }

        CashOutEvent cashOutEvent = new CashOutEvent();
        cashOutEvent.setCashBalance(cashBalance);
        cashOutEvent.setGastroTicketsBalance(gastroTicketsBalance);
        cashOutEvent.setTerminalBalance(terminalBalance);
        cashOutEvent.setCreatedAt(new Date());
        cashOutEvent.setCashDeskEvent(cashDeskEvent);

        if (endOfShift) {
            cashDeskEvent.setEndOfShift(new Date());
            cashOutEvent.setDailyBalance(true);
        } else {
            cashOutEvent.setDailyBalance(false);
        }

        cashDeskEvent.getCashOutEvents().add(cashOutEvent);
        return cashDeskEventRepository.save(cashDeskEvent);
    }

    private boolean isUserOnShift(Long userId) {
        CashDeskUser cashDeskUser = cashDeskUserRepository.findOne(userId);

        List<CashDeskEvent> cashDeskEvents = cashDeskUser.getCashDeskEvents();
        if (cashDeskEvents == null || cashDeskEvents.isEmpty()) {
            return false;
        }
        CashDeskEvent cashDeskEvent = cashDeskEvents.get(cashDeskEvents.size() - 1);

        return cashDeskEvent.getStartOfShift() != null && cashDeskEvent.getEndOfShift() == null;
    }
}
