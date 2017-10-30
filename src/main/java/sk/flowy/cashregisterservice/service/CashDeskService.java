package sk.flowy.cashregisterservice.service;


import sk.flowy.cashregisterservice.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.model.CashInWrapper;

public interface CashDeskService {

    public CashdeskEvent insertMoney(CashInWrapper cashInWrapper);

}
