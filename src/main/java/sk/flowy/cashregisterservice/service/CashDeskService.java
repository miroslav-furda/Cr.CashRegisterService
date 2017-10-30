package sk.flowy.cashregisterservice.service;


import sk.flowy.cashregisterservice.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.model.CashInWrapper;

public interface CashDeskService {

    public CashdeskEvent insertMoney(CashInWrapper cashInWrapper);
import sk.flowy.cashregisterservice.model.BalanceWrapper;
import sk.flowy.cashregisterservice.model.entity.CashDeskEvent;
import sk.flowy.cashregisterservice.model.entity.CashDeskUser;

import java.util.Optional;

/**
 *  Service for cashdesk actions.
 */
public interface CashDeskService {

    /**
     * Creates appropriate {@link CashDeskEvent} instance and persist it.
     *
     * @param balanceWrapper contains information about new interval or daily balance.
     * @return saved CashDeskEvent instance.
     */
    CashDeskEvent recordBalance(BalanceWrapper balanceWrapper);

    /**
     * Gets user currently on shift.
     *
     * @param userId id of user.
     * @return optional of either {@link CashDeskUser} or nothing if user is not currently on shift.
     */
    Optional<CashDeskUser> getUserFromCurrentShift(Long userId);

}
