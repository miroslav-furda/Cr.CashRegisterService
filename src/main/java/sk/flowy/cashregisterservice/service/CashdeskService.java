package sk.flowy.cashregisterservice.service;


public interface CashdeskService {

    public Long insertMoney(Long userId, Integer moneyValue, Long cashdeskEventId);

}
