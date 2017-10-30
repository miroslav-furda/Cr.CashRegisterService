package sk.flowy.cashregisterservice.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.flowy.cashregisterservice.exception.UserNotOnShiftException;
import sk.flowy.cashregisterservice.model.BalanceWrapper;
import sk.flowy.cashregisterservice.model.entity.CashDeskEvent;
import sk.flowy.cashregisterservice.model.entity.CashDeskUser;
import sk.flowy.cashregisterservice.model.entity.CashOutEvent;
import sk.flowy.cashregisterservice.repository.CashDeskEventRepository;
import sk.flowy.cashregisterservice.repository.CashDeskUserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    public  Optional<CashDeskUser> getUserFromCurrentShift(Long userId) {
        CashDeskUser cashDeskUser = cashDeskUserRepository.findOne(userId);

        if (cashDeskUser==null){
            return Optional.empty();
        }

        List<CashDeskEvent> cashDeskEvents = cashDeskUser.getCashDeskEvents();
        if (cashDeskEvents == null || cashDeskEvents.isEmpty()) {
            return Optional.empty();
        }
        CashDeskEvent cashDeskEvent = cashDeskEvents.get(cashDeskEvents.size() - 1);

        if (cashDeskEvent.getStartOfShift() == null || cashDeskEvent.getEndOfShift() != null){
            return Optional.empty();
        }

        return Optional.of(cashDeskUser);
    }
}
