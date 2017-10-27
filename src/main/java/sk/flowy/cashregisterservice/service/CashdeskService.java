package sk.flowy.cashregisterservice.service;


import sk.flowy.cashregisterservice.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.model.CashInWrapper;

public interface CashdeskService {

    public CashdeskEvent insertMoney(CashInWrapper cashInWrapper);

}
