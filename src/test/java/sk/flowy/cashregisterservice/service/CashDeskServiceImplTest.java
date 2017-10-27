package sk.flowy.cashregisterservice.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import sk.flowy.cashregisterservice.exception.UserNotOnShiftException;
import sk.flowy.cashregisterservice.model.entity.CashDeskEvent;
import sk.flowy.cashregisterservice.model.entity.CashDeskUser;
import sk.flowy.cashregisterservice.repository.CashDeskEventRepository;
import sk.flowy.cashregisterservice.repository.CashDeskUserRepository;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class CashDeskServiceImplTest {

    private static final Long USER_ON_SHIFT = 1L;
    private static final Long USER_NOT_ON_SHIFT = 2L;
    private static final int CASH = 100;
    private static final int TICKETS = 10;
    private static final int TERMINAL = 500;

    @MockBean
    private CashDeskUserRepository cashDeskUserRepository;
    @MockBean
    private CashDeskEventRepository cashDeskEventRepository;

    private CashDeskService cashDeskService;
    private CashDeskUser cashDeskUser;

    @Before
    public void setup() {
        CashDeskEvent cashDeskEvent = new CashDeskEvent();
        cashDeskEvent.setStartOfShift(new Date());
        cashDeskUser = new CashDeskUser();
        cashDeskUser.setCashDeskEvents(new ArrayList<>());
        cashDeskUser.getCashDeskEvents().add(cashDeskEvent);
        cashDeskService = new CashDeskServiceImpl(cashDeskUserRepository, cashDeskEventRepository);
    }

    @Test(expected = UserNotOnShiftException.class)
    public void if_user_is_not_on_shift_exception_is_thrown() {
        when(cashDeskUserRepository.findOne(USER_NOT_ON_SHIFT)).thenReturn(new CashDeskUser());
        cashDeskService.recordBalance(USER_NOT_ON_SHIFT, CASH, TICKETS, TERMINAL, false);

        verify(cashDeskEventRepository,never()).save(cashDeskUser.getCashDeskEvents().get(0));
    }

    @Test
    public void if_its_daily_balance_than_end_of_shift_is_set() {
        when(cashDeskUserRepository.findOne(USER_ON_SHIFT)).thenReturn(cashDeskUser);

        cashDeskService.recordBalance(USER_ON_SHIFT, CASH, TICKETS, TERMINAL, true);

        verify(cashDeskEventRepository).save(cashDeskUser.getCashDeskEvents().get(0));
        assertThat(cashDeskUser.getCashDeskEvents().get(0).getEndOfShift()).isNotNull();
        assertThat(cashDeskUser.getCashDeskEvents().get(0).getCashOutEvents().get(0).isDailyBalance()).isEqualTo(true);
    }

    @Test
    public void if_its_interval_balance_than_end_of_shift_stays_null() {
        when(cashDeskUserRepository.findOne(USER_ON_SHIFT)).thenReturn(cashDeskUser);

        cashDeskService.recordBalance(USER_ON_SHIFT, CASH, TICKETS, TERMINAL, false);

        verify(cashDeskEventRepository).save(cashDeskUser.getCashDeskEvents().get(0));
        assertThat(cashDeskUser.getCashDeskEvents().get(0).getEndOfShift()).isNull();
        assertThat(cashDeskUser.getCashDeskEvents().get(0).getCashOutEvents().get(0).isDailyBalance()).isEqualTo(false);
    }
}