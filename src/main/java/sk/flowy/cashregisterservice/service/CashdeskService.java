package sk.flowy.cashregisterservice.service;

import sk.flowy.cashregisterservice.model.entity.CashInEvent;
import sk.flowy.cashregisterservice.model.entity.CashOutEvent;
import sk.flowy.cashregisterservice.model.entity.CashdeskEvent;

public interface CashdeskService {

    boolean isUserOnShift(Long userId);

    CashdeskEvent doDailyBalance(Long userId, int cashBalance, int gastroTicketsBalance, int terminalBalance);

    CashdeskEvent doIntervalBalance(Long userId, int cashBalance, int gastroTicketsBalance, int terminalBalance);
}
