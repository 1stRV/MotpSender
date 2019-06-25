package ru.x5.motpsender.dao.helper;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;

import java.io.IOException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static ru.x5.motpsender.dao.helper.MotpTestConstants.*;

public class MotpSimpleGetHelper implements MotpHelper {

    private MediaType mediaType;
    private Resource mockedResponse;
    private String expectedUrl;
    private MockRestServiceServer mockRestServiceServer;

    public MotpSimpleGetHelper(Resource mockedResponse, String expectedUrl, MockRestServiceServer mockRestServiceServer) {
        this.mockedResponse = mockedResponse;
        this.expectedUrl = expectedUrl;
        this.mockRestServiceServer = mockRestServiceServer;
        this.mediaType =  MediaType.APPLICATION_JSON;
    }

    public void prepareTestData() throws IOException {
        String bodyExpected = IOUtils.toString(mockedResponse.getInputStream(), UTF8);
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(MockRestRequestMatchers.header(AUTH_HEADER, TOKEN_HEADER))
                .andRespond(withSuccess(bodyExpected, mediaType));
    }
}
