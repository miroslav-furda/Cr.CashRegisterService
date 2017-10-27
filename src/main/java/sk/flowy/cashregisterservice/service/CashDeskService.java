package sk.flowy.cashregisterservice.service;

import sk.flowy.cashregisterservice.model.entity.CashDeskEvent;

/**
 *  Service for cashdesk actions.
 */
public interface CashDeskService {

    /**
     * Creates appropriate {@link CashDeskEvent} instance and persist it.
     *
     * @param userId id of user on a shift
     * @param cashBalance cash balance
     * @param gastroTicketsBalance gastro ticket balance
     * @param terminalBalance terminal balance
     * @param endOfShift informs if its already end of shift or not.
     * @return saved CashDeskEvent instance.
     */
    CashDeskEvent recordBalance(Long userId, int cashBalance, int gastroTicketsBalance, int terminalBalance, boolean endOfShift);
}
