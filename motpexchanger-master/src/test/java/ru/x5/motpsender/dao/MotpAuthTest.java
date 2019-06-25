package ru.x5.motpsender.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.x5.motpsender.MotpApplicationTests;
import ru.x5.motpsender.dao.dto.AuthResponse;
import ru.x5.motpsender.dao.dto.TokenRequest;
import ru.x5.motpsender.dao.dto.TokenResponse;
import ru.x5.motpsender.dao.utilites.DataSigner;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class MotpAuthTest extends MotpApplicationTests {

    @Autowired
    private MotpAuth motpAuth;

    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private DataSigner dataSigner;

    @Value("classpath:json/token-response.json")
    private Resource tokenResponse;

    @Value("classpath:json/get-auth-response.json")
    private Resource authResponse;

    @Value("${motp.api.auth}")
    private String authExpectedUrl;

    @Value("${motp.api.sign}")
    private String tokenExpectedUrl;

    private MockRestServiceServer mockServer;

    private TokenResponse tokenResponseExpected;

    private AuthResponse authResponseExpected;

    private TokenRequest tokenRequestExpected;

    private static final String AUTH_DATA = "efd833c7248544dca05bff1036bea6";
    private static final String SIGNED_AUTH_DATA = "SIGNED_AUTH_DATA";


    @Before
    public void init() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();

        tokenRequestExpected = TokenRequest.builder().uuid(UUID.fromString("561c77a3-84b7-4102-a291-5626a4aa03f6"))
                .data(SIGNED_AUTH_DATA).build();

        authResponseExpected = AuthResponse.builder()
                .uuid(UUID.fromString("561c77a3-84b7-4102-a291-5626a4aa03f6"))
                .data(AUTH_DATA).build();
        String tokenExpected = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3OSIsImNu" +
                "Ijoi0K_QutC40LzQvtCy0L jRhyAg0K7RgNC40LkiLCJwaWQiOjExMjA5NSwiaW5uIjoiNzcxNTI1Mzk4MCIsIn Blcm1z" +
                "IjoiMDQ1MSIsImV4cCI6MTUzMjUzNjE0M30.9Ijmz8YEXJh-qM4bAN4Buu3HtQAKoP9MgAO7uUfUmdABu0npTaPLtFO1D_" +
                "FcT2R3CiJaS6d3RZWEJcnrDg69lQ";
        tokenResponseExpected = TokenResponse.builder().lifetime(1440).token(tokenExpected).build();
        when(dataSigner.signData(AUTH_DATA)).thenReturn(SIGNED_AUTH_DATA);
    }

    @Test
    public void getAuthCode() throws IOException {
        String authExpected = IOUtils.toString(authResponse.getInputStream(), UTF8);
        mockServer.expect(ExpectedCount.once(), requestTo(authExpectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(authExpected, MediaType.APPLICATION_JSON));
        AuthResponse authResponseActual = motpAuth.getAuthCode();
        assertEquals(authResponseExpected, authResponseActual);
    }

    @Test
    public void getToken() throws IOException {
        String authExpected = IOUtils.toString(authResponse.getInputStream(), UTF8);
        String tokenExpected = IOUtils.toString(tokenResponse.getInputStream(), UTF8);
        mockServer.expect(ExpectedCount.once(), requestTo(authExpectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(authExpected, MediaType.APPLICATION_JSON));
        mockServer.expect(ExpectedCount.once(), requestTo(tokenExpectedUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(mapper.writeValueAsString(tokenRequestExpected)))
                .andRespond(withSuccess(tokenExpected, MediaType.APPLICATION_JSON));
        TokenResponse tokenResponseActual = motpAuth.getToken();
        verify(dataSigner).signData(AUTH_DATA);
        assertEquals(tokenResponseExpected, tokenResponseActual);

    }
}