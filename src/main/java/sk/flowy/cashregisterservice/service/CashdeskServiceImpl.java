package sk.flowy.cashregisterservice.service;

import lombok.extern.log4j.Log4j;
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

/**
 * Default {@link CashdeskService} implementation.
 */
@Service
@Log4j
public class CashdeskServiceImpl implements CashdeskService {

    private final CashdeskUserRepository cashdeskUserRepository;
    private final CashdeskEventRepository cashdeskEventRepository;

    /**
     * Constructor.
     *
     * @param cashdeskUserRepository repository for amnipulating with CashdeskUser table.
     * @param cashdeskEventRepository repository for amnipulating with CashdeskEvent table.
     */
    @Autowired
    public CashdeskServiceImpl(CashdeskUserRepository cashdeskUserRepository, CashdeskEventRepository
            cashdeskEventRepository) {
        this.cashdeskUserRepository = cashdeskUserRepository;
        this.cashdeskEventRepository = cashdeskEventRepository;
    }


    @Override
    public CashdeskEvent recordBalance(Long userId, int cashBalance, int gastroTicketsBalance, int terminalBalance,
                                       boolean endOfShift) {
        if (!isUserOnShift(userId)) {
            log.warn("User " + userId + " has not began any shift yet.");
            throw new UserNotOnShiftException();
        }
        CashdeskUser cashdeskUser = cashdeskUserRepository.findOne(userId);
        CashdeskEvent cashdeskEvent = cashdeskUser.getCashdeskEvents().get(cashdeskUser.getCashdeskEvents().size() - 1);

        if (cashdeskEvent.getCashOutEvents() == null) {
            cashdeskEvent.setCashOutEvents(new ArrayList<>());
        }

        CashOutEvent cashOutEvent = new CashOutEvent();
        cashOutEvent.setCashBalance(cashBalance);
        cashOutEvent.setGastroTicketsBalance(gastroTicketsBalance);
        cashOutEvent.setTerminalBalance(terminalBalance);
        cashOutEvent.setCreatedAt(new Date());
        cashOutEvent.setCashdeskEvent(cashdeskEvent);

        if (endOfShift) {
            cashdeskEvent.setEndOfShift(new Date());
            cashOutEvent.setDailyBalance(true);
        } else {
            cashOutEvent.setDailyBalance(false);
        }

        cashdeskEvent.getCashOutEvents().add(cashOutEvent);
        return cashdeskEventRepository.save(cashdeskEvent);
    }

    private boolean isUserOnShift(Long userId) {
        CashdeskUser cashdeskUser = cashdeskUserRepository.findOne(userId);

        List<CashdeskEvent> cashdeskEvents = cashdeskUser.getCashdeskEvents();
        if (cashdeskEvents == null || cashdeskEvents.isEmpty()) {
            return false;
        }
        CashdeskEvent cashdeskEvent = cashdeskEvents.get(cashdeskEvents.size() - 1);

        return cashdeskEvent.getStartOfShift() != null && cashdeskEvent.getEndOfShift() == null;
    }
}
