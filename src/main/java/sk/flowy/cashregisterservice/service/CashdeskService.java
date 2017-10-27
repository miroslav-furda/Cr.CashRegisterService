package sk.flowy.cashregisterservice.service;


import sk.flowy.cashregisterservice.entity.CashdeskEvent;

public interface CashdeskService {

    public CashdeskEvent insertMoney(Long userId, Integer moneyValue);

}
