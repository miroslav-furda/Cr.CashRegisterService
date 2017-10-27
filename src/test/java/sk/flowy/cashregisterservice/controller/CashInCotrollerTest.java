package sk.flowy.cashregisterservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import sk.flowy.cashregisterservice.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.entity.CashdeskUser;
import sk.flowy.cashregisterservice.model.CashInWrapper;
import sk.flowy.cashregisterservice.service.CashdeskService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CashInController.class, secure = false)
public class CashInCotrollerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CashdeskService cashdeskService;

    private final static Long INVALID_USER_ID = 0L;

    private static final String VALID_TOKEN = "Bearer " +
            "PxQDVFVRCQITVlZRDgcFV0YdFGQHBDcQUQxLA1tMXV1dSnwZQRNWERdcRE42LGtnH0tLQBsBGVZQABoDTFFaVAhRDVsLCA0LClwAWAhNEVVTDhVNRAcAV1RTVlEBV1YCBwAGEU4AAg5DB2U=";

    @Test
    public void if_not_all_inputs_are_provided_throw_lack_of_info_exception() throws Exception{
        CashdeskEvent cashdeskEventMock = mock(CashdeskEvent.class);
        CashInWrapper cashInWrapper = new CashInWrapper();
        cashInWrapper.setUserId(INVALID_USER_ID);
        cashInWrapper.setBalance(200);

        when(cashdeskService.insertMoney(cashInWrapper)).thenReturn(cashdeskEventMock);

        mvc.perform(get("/api/flowy/cashin/").header(AUTHORIZATION, VALID_TOKEN).contentType(APPLICATION_JSON)
                .content(asJsonString(cashInWrapper)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void if_cashdesk_event_is_null_return_internal_server_error() throws Exception{

    }

    private String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
