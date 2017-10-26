package sk.flowy.cashregisterservice.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.flowy.cashregisterservice.entity.CashInEvent;
import sk.flowy.cashregisterservice.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.entity.CashdeskUser;
import sk.flowy.cashregisterservice.exception.CashdeskEventNotFoundException;
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
    public Long insertMoney(Long userId, Integer moneyValue, Long cashdeskEventId) {
        Date createdAt = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        CashdeskEvent shift;
        if (cashdeskEventId == null) {
            shift = createShift(userId, createdAt);
        } else {
            shift = getShift(cashdeskEventId);
        }

        CashInEvent cashInEvent = new CashInEvent();
        cashInEvent.setBalance(moneyValue);
        cashInEvent.setCreatedAt(createdAt);

        if (shift != null) {
            List<CashInEvent> cashInEvents = shift.getCashInEvents();
            cashInEvents.add(cashInEvent);

            cashdeskEventRepository.save(shift);
            return shift.getId();
        } else {
            throw new CashdeskEventNotFoundException();
        }

    }

    private CashdeskEvent getShift(Long cashdeskEventId) {
        return cashdeskEventRepository.findOne(cashdeskEventId);
    }

    private CashdeskEvent createShift(Long userId, Date startOfShift) {
        CashdeskEvent cashdeskEvent = new CashdeskEvent();
        CashdeskUser cashdeskUser = cashdeskUserRepository.findOne(userId);
        List<CashInEvent> cashInEvents = new ArrayList<>();

        cashdeskEvent.setCashdeskUser(cashdeskUser);
        cashdeskEvent.setStartOfShift(startOfShift);
        cashdeskEvent.setCashInEvents(cashInEvents);

        return cashdeskEventRepository.save(cashdeskEvent);
    }

}
