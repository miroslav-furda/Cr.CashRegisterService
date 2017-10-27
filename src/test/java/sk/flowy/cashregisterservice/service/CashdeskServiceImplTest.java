package sk.flowy.cashregisterservice.service;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import sk.flowy.cashregisterservice.exception.UserNotOnShiftException;
import sk.flowy.cashregisterservice.model.entity.CashOutEvent;
import sk.flowy.cashregisterservice.model.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.model.entity.CashdeskUser;
import sk.flowy.cashregisterservice.repository.CashdeskEventRepository;
import sk.flowy.cashregisterservice.repository.CashdeskUserRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class CashdeskServiceImplTest {

    private static final Long USER_ON_SHIFT = 1L;
    private static final Long USER_NOT_ON_SHIFT = 2L;
    private static final int CASH = 100;
    private static final int TICKETS = 10;
    private static final int TERMINAL = 500;

    @MockBean
    private CashdeskUserRepository cashdeskUserRepository;
    @MockBean
    private CashdeskEventRepository cashdeskEventRepository;

    private CashdeskService cashdeskService;
    private  CashdeskUser cashdeskUser;

    @Before
    public void setup() {
        CashdeskEvent cashdeskEvent = new CashdeskEvent();
        cashdeskEvent.setStartOfShift(new Date());
        cashdeskUser = new CashdeskUser();
        cashdeskUser.setCashdeskEvents(new ArrayList<>());
        cashdeskUser.getCashdeskEvents().add(cashdeskEvent);
        cashdeskService = new CashdeskServiceImpl(cashdeskUserRepository, cashdeskEventRepository);
    }

    @Test(expected = UserNotOnShiftException.class)
    public void if_user_is_not_on_shift_exception_is_thrown() {
        when(cashdeskUserRepository.findOne(USER_NOT_ON_SHIFT)).thenReturn(new CashdeskUser());
        cashdeskService.recordBalance(USER_NOT_ON_SHIFT, CASH, TICKETS, TERMINAL, false);

        verify(cashdeskEventRepository,never()).save(cashdeskUser.getCashdeskEvents().get(0));
    }

    @Test
    public void if_its_daily_balance_than_end_of_shift_is_set() {
        when(cashdeskUserRepository.findOne(USER_ON_SHIFT)).thenReturn(cashdeskUser);

        cashdeskService.recordBalance(USER_ON_SHIFT, CASH, TICKETS, TERMINAL, true);

        verify(cashdeskEventRepository).save(cashdeskUser.getCashdeskEvents().get(0));
        assertThat(cashdeskUser.getCashdeskEvents().get(0).getEndOfShift()).isNotNull();
        assertThat(cashdeskUser.getCashdeskEvents().get(0).getCashOutEvents().get(0).isDailyBalance()).isEqualTo(true);
    }

    @Test
    public void if_its_interval_balance_than_end_of_shift_stays_null() {
        when(cashdeskUserRepository.findOne(USER_ON_SHIFT)).thenReturn(cashdeskUser);

        cashdeskService.recordBalance(USER_ON_SHIFT, CASH, TICKETS, TERMINAL, false);

        verify(cashdeskEventRepository).save(cashdeskUser.getCashdeskEvents().get(0));
        assertThat(cashdeskUser.getCashdeskEvents().get(0).getEndOfShift()).isNull();
        assertThat(cashdeskUser.getCashdeskEvents().get(0).getCashOutEvents().get(0).isDailyBalance()).isEqualTo(false);
    }
}