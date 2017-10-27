package sk.flowy.cashregisterservice.service;

import sk.flowy.cashregisterservice.model.entity.CashInEvent;
import sk.flowy.cashregisterservice.model.entity.CashOutEvent;
import sk.flowy.cashregisterservice.model.entity.CashdeskEvent;

/**
 *  Service for cashdesk actions.
 */
public interface CashdeskService {

    /**
     * Creates appropriate {@link CashdeskEvent} instance and persist it.
     *
     * @param userId id of user on a shift
     * @param cashBalance cash balance
     * @param gastroTicketsBalance gastro ticket balance
     * @param terminalBalance terminal balance
     * @param endOfShift informs if its already end of shift or not.
     * @return saved CashDeskEvent instance.
     */
    CashdeskEvent recordBalance(Long userId, int cashBalance, int gastroTicketsBalance, int terminalBalance, boolean endOfShift);
}
