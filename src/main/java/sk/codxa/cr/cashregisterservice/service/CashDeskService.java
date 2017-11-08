package sk.codxa.cr.cashregisterservice.service;

import sk.codxa.cr.cashregisterservice.model.CashInWrapper;
import sk.codxa.cr.cashregisterservice.model.entity.CashDeskEvent;
import sk.codxa.cr.cashregisterservice.model.BalanceWrapper;

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
