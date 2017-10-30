package sk.flowy.cashregisterservice.service;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import sk.flowy.cashregisterservice.model.entity.CashInEvent;
import sk.flowy.cashregisterservice.model.entity.CashDeskEvent;
import sk.flowy.cashregisterservice.model.entity.CashDeskUser;
import sk.flowy.cashregisterservice.exception.CashDeskUserNotFoundException;
import sk.flowy.cashregisterservice.model.CashInWrapper;
import sk.flowy.cashregisterservice.repository.CashDeskEventRepository;
import sk.flowy.cashregisterservice.repository.CashDeskUserRepository;
import sk.flowy.cashregisterservice.exception.UserNotOnShiftException;
import sk.flowy.cashregisterservice.model.BalanceWrapper;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashDeskServiceImpl.class)
public class CashDeskServiceImplTest {

    @MockBean
    private CashDeskEventRepository cashDeskEventRepository;
    @MockBean
    private CashDeskUserRepository cashDeskUserRepository;
    private CashDeskServiceImpl cashDeskService;

    private final static Long NON_EXISTING_USER = 0L;
    private final static Long EXISTING_USER = 1L;

    private static final Long USER_ON_SHIFT = 1L;
    private static final Long USER_NOT_ON_SHIFT = 2L;
    private static final int CASH = 100;
    private static final int TICKETS = 10;
    private static final int TERMINAL = 500;

    private CashDeskUser cashDeskUser;
    private BalanceWrapper balanceWrapper;

    @Before
        public void setup() {
        cashDeskService = new CashDeskServiceImpl(cashDeskEventRepository, cashDeskUserRepository);

        CashDeskEvent cashDeskEvent = new CashDeskEvent();
        cashDeskEvent.setStartOfShift(new Date());
        cashDeskUser = new CashDeskUser();
        cashDeskUser.setCashDeskEvents(new ArrayList<>());
        cashDeskUser.getCashDeskEvents().add(cashDeskEvent);
        cashDeskService = new CashDeskServiceImpl(cashDeskEventRepository, cashDeskUserRepository);
    }

    @Test
    public void if_shift_is_starting_create_new_and_insert_money() {
        CashDeskEvent cashdeskEventNew = new CashDeskEvent();
        cashdeskEventNew.setStartOfShift(new Date());
        cashdeskEventNew.setCashInEvents(new ArrayList<>());

        CashInWrapper cashInWrapper = new CashInWrapper();
        cashInWrapper.setUserId(EXISTING_USER);
        cashInWrapper.setBalance(1000);

        CashDeskUser cashdeskUserStartingShift;
        cashdeskUserStartingShift = new CashDeskUser();
        cashdeskUserStartingShift.setCashDeskEvents(new ArrayList<>());
        cashdeskUserStartingShift.getCashDeskEvents().add(cashdeskEventNew);

        when(cashDeskUserRepository.findOne(EXISTING_USER)).thenReturn(cashdeskUserStartingShift);

        Assertions.assertThat(cashdeskUserStartingShift.getCashDeskEvents().get(0).getCashInEvents().isEmpty());
        cashDeskService.insertMoney(cashInWrapper);
        verify(cashDeskEventRepository).save(cashdeskUserStartingShift.getCashDeskEvents().get(0));
        Assertions.assertThat(cashdeskUserStartingShift.getCashDeskEvents().get(0).getCashInEvents().size()).isEqualTo(1);
        Assertions.assertThat(cashdeskUserStartingShift.getCashDeskEvents().get(0).getCashInEvents().get(0).getBalance()).isEqualTo(1000);
    }

    @Test
    public void if_shift_is_running_insert_additional_cashin() {
        CashInEvent cashInEvent = new CashInEvent();
        cashInEvent.setCreatedAt(new Date());
        cashInEvent.setBalance(500);
        cashInEvent.setId(1L);

        CashDeskEvent cashdeskEventRunning = new CashDeskEvent();
        cashdeskEventRunning.setStartOfShift(new Date());
        cashdeskEventRunning.setCashInEvents(new ArrayList<>());
        cashInEvent.setCashDeskEvent(cashdeskEventRunning);
        cashdeskEventRunning.getCashInEvents().add(cashInEvent);

        CashInWrapper cashInWrapper = new CashInWrapper();
        cashInWrapper.setUserId(EXISTING_USER);
        cashInWrapper.setBalance(333);

        CashDeskUser cashdeskUserRunningShift;
        cashdeskUserRunningShift = new CashDeskUser();
        cashdeskUserRunningShift.setCashDeskEvents(new ArrayList<>());
        cashdeskUserRunningShift.getCashDeskEvents().add(cashdeskEventRunning);

        when(cashDeskUserRepository.findOne(EXISTING_USER)).thenReturn(cashdeskUserRunningShift);
        Assertions.assertThat(cashdeskUserRunningShift.getCashDeskEvents().get(0).getCashInEvents().size()).isEqualTo(1);
        cashDeskService.insertMoney(cashInWrapper);
        verify(cashDeskEventRepository).save(cashdeskUserRunningShift.getCashDeskEvents().get(0));
        Assertions.assertThat(cashdeskUserRunningShift.getCashDeskEvents().get(0).getCashInEvents().size()).isEqualTo(2);
        Assertions.assertThat(cashdeskUserRunningShift.getCashDeskEvents().get(0).getCashInEvents().get(1).getBalance()).isEqualTo(333);
    }

    @Test(expected = CashDeskUserNotFoundException.class)
    public void if_user_is_not_found_or_not_existing_throw_cashdesk_user_not_found_exception() {
        CashInWrapper cashInWrapper = new CashInWrapper();
        cashInWrapper.setUserId(NON_EXISTING_USER);
        cashInWrapper.setBalance(1000);

        when(cashDeskUserRepository.findOne(NON_EXISTING_USER)).thenReturn(null);
        cashDeskService.insertMoney(cashInWrapper);
        verify(cashDeskEventRepository).save(new CashDeskUser().getCashDeskEvents().get(0));
    }

    @Test(expected = UserNotOnShiftException.class)
    public void if_user_is_not_on_shift_exception_is_thrown() {
        balanceWrapper = new BalanceWrapper(USER_NOT_ON_SHIFT, CASH, TICKETS, TERMINAL, false);
        when(cashDeskUserRepository.findOne(USER_NOT_ON_SHIFT)).thenReturn(new CashDeskUser());
        cashDeskService.recordBalance(balanceWrapper);

        verify(cashDeskEventRepository,never()).save(cashDeskUser.getCashDeskEvents().get(0));
    }

    @Test
    public void if_its_daily_balance_than_end_of_shift_is_set() {
        balanceWrapper = new BalanceWrapper(USER_ON_SHIFT, CASH, TICKETS, TERMINAL, true);
        when(cashDeskUserRepository.findOne(USER_ON_SHIFT)).thenReturn(cashDeskUser);

        cashDeskService.recordBalance(balanceWrapper);

        verify(cashDeskEventRepository).save(cashDeskUser.getCashDeskEvents().get(0));
        assertThat(cashDeskUser.getCashDeskEvents().get(0).getEndOfShift()).isNotNull();
        assertThat(cashDeskUser.getCashDeskEvents().get(0).getCashOutEvents().get(0).isDailyBalance()).isEqualTo(true);
    }

    @Test
    public void if_its_interval_balance_than_end_of_shift_stays_null() {
        balanceWrapper = new BalanceWrapper(USER_ON_SHIFT, CASH, TICKETS, TERMINAL, false);
        when(cashDeskUserRepository.findOne(USER_ON_SHIFT)).thenReturn(cashDeskUser);

        cashDeskService.recordBalance(balanceWrapper);

        verify(cashDeskEventRepository).save(cashDeskUser.getCashDeskEvents().get(0));
        assertThat(cashDeskUser.getCashDeskEvents().get(0).getEndOfShift()).isNull();
        assertThat(cashDeskUser.getCashDeskEvents().get(0).getCashOutEvents().get(0).isDailyBalance()).isEqualTo(false);
    }
}




