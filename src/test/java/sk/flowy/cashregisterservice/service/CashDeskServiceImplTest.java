package sk.flowy.cashregisterservice.service;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import sk.flowy.cashregisterservice.entity.CashInEvent;
import sk.flowy.cashregisterservice.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.entity.CashdeskUser;
import sk.flowy.cashregisterservice.exception.CashDeskUserNotFoundException;
import sk.flowy.cashregisterservice.model.CashInWrapper;
import sk.flowy.cashregisterservice.repository.CashdeskEventRepository;
import sk.flowy.cashregisterservice.repository.CashdeskUserRepository;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashDeskServiceImpl.class)
public class CashDeskServiceImplTest {

    @MockBean
    private CashdeskEventRepository cashdeskEventRepository;
    @MockBean
    private CashdeskUserRepository cashdeskUserRepository;
    private CashDeskServiceImpl cashdeskService;

    private final static Long NON_EXISTING_USER = 0L;
    private final static Long EXISTING_USER = 1L;

    @Before
        public void setup() {
        cashdeskService = new CashDeskServiceImpl(cashdeskEventRepository,cashdeskUserRepository);
    }

    @Test
    public void if_shift_is_starting_create_new_and_insert_money() {
        CashdeskEvent cashdeskEventNew = new CashdeskEvent();
        cashdeskEventNew.setStartOfShift(new Date());
        cashdeskEventNew.setCashInEvents(new ArrayList<>());

        CashInWrapper cashInWrapper = new CashInWrapper();
        cashInWrapper.setUserId(EXISTING_USER);
        cashInWrapper.setBalance(1000);

        CashdeskUser cashdeskUserStartingShift;
        cashdeskUserStartingShift = new CashdeskUser();
        cashdeskUserStartingShift.setCashdeskEvents(new ArrayList<>());
        cashdeskUserStartingShift.getCashdeskEvents().add(cashdeskEventNew);

        when(cashdeskUserRepository.findOne(EXISTING_USER)).thenReturn(cashdeskUserStartingShift);

        Assertions.assertThat(cashdeskUserStartingShift.getCashdeskEvents().get(0).getCashInEvents().isEmpty());
        cashdeskService.insertMoney(cashInWrapper);
        verify(cashdeskEventRepository).save(cashdeskUserStartingShift.getCashdeskEvents().get(0));
        Assertions.assertThat(cashdeskUserStartingShift.getCashdeskEvents().get(0).getCashInEvents().size()).isEqualTo(1);
        Assertions.assertThat(cashdeskUserStartingShift.getCashdeskEvents().get(0).getCashInEvents().get(0).getBalance()).isEqualTo(1000);
    }

    @Test
    public void if_shift_is_running_insert_additional_cashin() {
        CashInEvent cashInEvent = new CashInEvent();
        cashInEvent.setCreatedAt(new Date());
        cashInEvent.setBalance(500);
        cashInEvent.setId(1L);

        CashdeskEvent cashdeskEventRunning = new CashdeskEvent();
        cashdeskEventRunning.setStartOfShift(new Date());
        cashdeskEventRunning.setCashInEvents(new ArrayList<>());
        cashInEvent.setCashdeskEvent(cashdeskEventRunning);
        cashdeskEventRunning.getCashInEvents().add(cashInEvent);

        CashInWrapper cashInWrapper = new CashInWrapper();
        cashInWrapper.setUserId(EXISTING_USER);
        cashInWrapper.setBalance(333);

        CashdeskUser cashdeskUserRunningShift;
        cashdeskUserRunningShift = new CashdeskUser();
        cashdeskUserRunningShift.setCashdeskEvents(new ArrayList<>());
        cashdeskUserRunningShift.getCashdeskEvents().add(cashdeskEventRunning);

        when(cashdeskUserRepository.findOne(EXISTING_USER)).thenReturn(cashdeskUserRunningShift);
        Assertions.assertThat(cashdeskUserRunningShift.getCashdeskEvents().get(0).getCashInEvents().size()).isEqualTo(1);
        cashdeskService.insertMoney(cashInWrapper);
        verify(cashdeskEventRepository).save(cashdeskUserRunningShift.getCashdeskEvents().get(0));
        Assertions.assertThat(cashdeskUserRunningShift.getCashdeskEvents().get(0).getCashInEvents().size()).isEqualTo(2);
        Assertions.assertThat(cashdeskUserRunningShift.getCashdeskEvents().get(0).getCashInEvents().get(1).getBalance()).isEqualTo(333);
    }

    @Test(expected = CashDeskUserNotFoundException.class)
    public void if_user_is_not_found_or_not_existing_throw_cashdesk_user_not_found_exception() {
        CashInWrapper cashInWrapper = new CashInWrapper();
        cashInWrapper.setUserId(NON_EXISTING_USER);
        cashInWrapper.setBalance(1000);

        when(cashdeskUserRepository.findOne(NON_EXISTING_USER)).thenReturn(null);
        cashdeskService.insertMoney(cashInWrapper);
        verify(cashdeskEventRepository).save(new CashdeskUser().getCashdeskEvents().get(0));
    }
}
