package sk.flowy.cashregisterservice.service;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import sk.flowy.cashregisterservice.entity.CashInEvent;
import sk.flowy.cashregisterservice.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.entity.CashdeskUser;
import sk.flowy.cashregisterservice.model.CashInWrapper;
import sk.flowy.cashregisterservice.repository.CashdeskEventRepository;
import sk.flowy.cashregisterservice.repository.CashdeskUserRepository;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CashdeskServiceImpl.class)
public class CashdeskServiceImplTest {

    @MockBean
    private CashdeskEventRepository cashdeskEventRepository;
    @MockBean
    private CashdeskUserRepository cashdeskUserRepository;
    private CashdeskServiceImpl cashdeskService;

    private final static Long nonExistingUser = 0L;
    private final static Long existingUser = 1L;

    @Before
    public void setup() {
        cashdeskService = new CashdeskServiceImpl(cashdeskEventRepository,cashdeskUserRepository);
    }

    @Test
    public void if_shift_is_starting_create_new_and_insert_money() {
        CashInWrapper cashInWrapper = new CashInWrapper();
        cashInWrapper.setUserId(existingUser);
        cashInWrapper.setBalance(1000);

        CashdeskUser cashdeskUser = new CashdeskUser();

        when(cashdeskUserRepository.findOne(existingUser)).thenReturn(cashdeskUser);

        CashdeskEvent cashdeskEventWithShift = cashdeskService.insertMoney(cashInWrapper);
        Assertions.assertThat(cashdeskEventWithShift.getCashInEvents()).isNotEmpty();
    }

    @Test
    public void if_shift_is_running_insert_money_cash_in() {

    }

    @Test
    public void if_user_is_not_found_or_not_existing_throw_cashdesk_user_not_found_exception() {

    }
}
