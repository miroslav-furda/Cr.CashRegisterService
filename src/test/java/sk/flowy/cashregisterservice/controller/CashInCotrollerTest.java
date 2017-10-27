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
import sk.flowy.cashregisterservice.entity.CashdeskEvent;
import sk.flowy.cashregisterservice.model.CashInWrapper;
import sk.flowy.cashregisterservice.security.CallResponse;
import sk.flowy.cashregisterservice.security.TokenRepository;
import sk.flowy.cashregisterservice.service.CashdeskService;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CashInController.class, secure = false)
public class CashInCotrollerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CashdeskService cashdeskService;

    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private RestTemplateBuilder restTemplateBuilder;

    private static final String VALID_TOKEN = "Bearer " +
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjYyLCJpc3MiOiJodHRwOlwvXC9sYWJhcy5wcm9taXNlby5jb21cL2FwcFwvcHVibGljXC9hcGlcL2F1dGhlbnRpY2F0ZSIsImlhdCI6MTUwOTA5MTM1OCwiZXhwIjoxNTA5Mjg2OTU4LCJuYmYiOjE1MDkwOTEzNTgsImp0aSI6ImQ3NzMwODUwYmEyZjE2MDJkYWI2NDUxMTFjNGVlN2Q4In0.lakKjPivPuNW4Kol7Kvx9uCzLGRZVpB4JbQK8o5YeoM";

    @Before
    public void setup() {
        when(tokenRepository.checkTokenValidity(anyString())).thenReturn(new CallResponse("success", null));
    }

    @Test
    public void if_not_all_inputs_are_provided_throw_lack_of_info_exception() throws Exception{
        CashdeskEvent cashdeskEventMock = mock(CashdeskEvent.class);
        CashInWrapper cashInWrapper = new CashInWrapper();

        when(cashdeskService.insertMoney(cashInWrapper)).thenReturn(cashdeskEventMock);

        mvc.perform(post("/api/flowy/cashin").header(AUTHORIZATION, VALID_TOKEN).contentType(APPLICATION_JSON)
                .content(asJsonString(cashInWrapper)))
                .andExpect(status().isBadRequest());

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
