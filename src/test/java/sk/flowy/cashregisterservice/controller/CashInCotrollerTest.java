package sk.flowy.cashregisterservice.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import sk.flowy.cashregisterservice.service.CashdeskService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CashInController.class, secure = false)
public class CashInCotrollerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CashdeskService cashdeskService;

    @Test
    public void if_not_all_inputs_are_provided_throw_lack_of_info_exception() throws Exception{

    }

    @Test
    public void if_cashdesk_event_is_null_return_internal_server_error() throws Exception{

    }

}
