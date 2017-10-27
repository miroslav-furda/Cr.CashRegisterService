package sk.flowy.cashregisterservice.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.flowy.cashregisterservice.entity.CashInEvent;
import sk.flowy.cashregisterservice.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.entity.CashdeskUser;
import sk.flowy.cashregisterservice.exception.CashdeskUserNotFoundException;
import sk.flowy.cashregisterservice.model.CashInWrapper;
import sk.flowy.cashregisterservice.repository.CashdeskEventRepository;
import sk.flowy.cashregisterservice.repository.CashdeskUserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Log4j
@Service
public class CashdeskServiceImpl implements CashdeskService {

    private final CashdeskEventRepository cashdeskEventRepository;

    private final CashdeskUserRepository cashdeskUserRepository;

    @Autowired
    public CashdeskServiceImpl(CashdeskEventRepository cashdeskEventRepository, CashdeskUserRepository cashdeskUserRepository) {
        this.cashdeskEventRepository = cashdeskEventRepository;
        this.cashdeskUserRepository = cashdeskUserRepository;
    }

    @Override
    public CashdeskEvent insertMoney(CashInWrapper cashInWrapper) {
        Date createdAt = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        CashdeskEvent shift = getShift(cashInWrapper.getUserId());

        CashInEvent cashInEvent = new CashInEvent();
        cashInEvent.setBalance(cashInWrapper.getBalance());
        cashInEvent.setCreatedAt(createdAt);
        cashInEvent.setCashdeskEvent(shift);

        List<CashInEvent> cashInEvents = shift.getCashInEvents();
        cashInEvents.add(cashInEvent);

        return cashdeskEventRepository.save(shift);
    }

    private CashdeskEvent getShift(Long userId) {

        CashdeskUser cashdeskUser = cashdeskUserRepository.findOne(userId);
        if ( cashdeskUser != null) {
            List<CashdeskEvent> cashdeskEvents = cashdeskUser.getCashdeskEvents();
            if (cashdeskEvents == null || cashdeskEvents.isEmpty()) {
                Date createdAt = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
                return createShift(userId, createdAt);
            } else {
                CashdeskEvent lastShift = cashdeskEvents.get(cashdeskEvents.size() - 1);
                if (lastShift.getEndOfShift() != null) {
                    Date createdAt = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
                    return createShift(userId, createdAt);
                } else {
                    return lastShift;
                }
            }
        } else {
            throw new CashdeskUserNotFoundException();
        }
    }

    private CashdeskEvent createShift(Long userId, Date startOfShift) {
        CashdeskEvent cashdeskEvent = new CashdeskEvent();
        CashdeskUser cashdeskUser = cashdeskUserRepository.findOne(userId);
        List<CashInEvent> cashInEvents = new ArrayList<>();

        cashdeskEvent.setCashdeskUser(cashdeskUser);
        cashdeskEvent.setStartOfShift(startOfShift);
        cashdeskEvent.setCashInEvents(cashInEvents);

        return cashdeskEvent;
    }

}
