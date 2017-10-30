package sk.flowy.cashregisterservice.service;

import sk.flowy.cashregisterservice.model.CashInWrapper;
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
     *
     * @param cashInWrapper holding information about cashflow
     * @return saved CashDeskEvent instance.
     */
    CashDeskEvent insertMoney(CashInWrapper cashInWrapper);

}
