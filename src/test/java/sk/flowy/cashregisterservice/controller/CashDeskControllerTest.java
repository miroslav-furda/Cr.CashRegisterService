package sk.flowy.cashregisterservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import sk.flowy.cashregisterservice.model.BalanceWrapper;
import sk.flowy.cashregisterservice.model.CashInWrapper;
import sk.flowy.cashregisterservice.model.entity.CashDeskEvent;
import sk.flowy.cashregisterservice.security.CallResponse;
import sk.flowy.cashregisterservice.security.TokenRepository;
import sk.flowy.cashregisterservice.service.CashDeskService;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CashDeskController.class)
public class CashDeskControllerTest {

    private static final String VALID_TOKEN = "Bearer " +
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjYyLCJpc3MiOiJodHRwOlwvXC9sYWJhcy5wcm9taXNlby5jb21cL2FwcFwvcHVibGljXC9hcGlcL2F1dGhlbnRpY2F0ZSIsImlhdCI6MTUwODIzMjIyMSwiZXhwIjoxNTA4NDI0MjIxLCJuYmYiOjE1MDgyMzIyMjEsImp0aSI6IjdlMmQ1NTg2Yzg4YjAzMzE0NDAzZDRmM2U3ODIyMjAzIn0.ujt2PXYrlmHVcL1GZo5ByZHcHdBaPr-dkCgtvBx8uG8";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CashDeskService cashDeskService;
    @MockBean
    private TokenRepository tokenRepository;
    @MockBean
    private RestTemplateBuilder restTemplateBuilder;

    @Before
    public void setup() {
        when(tokenRepository.checkTokenValidity(anyString())).thenReturn(new CallResponse("success", null));
    }

    @Test
    public void when_controller_is_called_with_wrong_inputs_exception_is_thrown() throws Exception {
        BalanceWrapper wrapper = new BalanceWrapper();
        wrapper.setUserId(null);

        mvc.perform(post("/api/recordBalance").header(AUTHORIZATION, VALID_TOKEN)
                .contentType(APPLICATION_JSON)
                .content(asJsonString(wrapper)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void when_recordBalance_gets_right_inputs_than_cashdeskEvent_is_returned() throws Exception {
        CashDeskEvent cashDeskEvent = new CashDeskEvent();
        when(cashDeskService.recordBalance(any(BalanceWrapper.class)))
                .thenReturn(cashDeskEvent);
        BalanceWrapper wrapper = new BalanceWrapper();
        wrapper.setCashBalance(200);
        wrapper.setEndOfShift(false);
        wrapper.setGastroTicketsBalance(5);
        wrapper.setTerminalBalance(500);
        wrapper.setUserId(5L);

        mvc.perform(post("/api/recordBalance").header(AUTHORIZATION, VALID_TOKEN).contentType(APPLICATION_JSON)
                .content(asJsonString(wrapper)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(cashDeskEvent)));

        verify(cashDeskService).recordBalance(wrapper);
    }

    @Test
    public void if_not_all_inputs_are_provided_throw_lack_of_info_exception() throws Exception{
        CashDeskEvent cashDeskEvent = new CashDeskEvent();
        CashInWrapper cashInWrapper = new CashInWrapper();

        when(cashDeskService.insertMoney(cashInWrapper)).thenReturn(cashDeskEvent);

        mvc.perform(post("/api/cashin").header(AUTHORIZATION, VALID_TOKEN).contentType(APPLICATION_JSON)
                .content(asJsonString(cashInWrapper)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void if_all_inputs_are_correct_then() throws Exception{
        CashDeskEvent cashDeskEvent = new CashDeskEvent();
        CashInWrapper cashInWrapper = new CashInWrapper(9L, 200);

        when(cashDeskService.insertMoney(cashInWrapper)).thenReturn(cashDeskEvent);

        mvc.perform(post("/api/cashin").header(AUTHORIZATION, VALID_TOKEN).contentType(APPLICATION_JSON)
                .content(asJsonString(cashInWrapper)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(cashDeskEvent)));

        verify(cashDeskService).insertMoney(cashInWrapper);
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