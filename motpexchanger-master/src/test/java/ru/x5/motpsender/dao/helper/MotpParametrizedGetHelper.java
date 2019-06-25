package ru.x5.motpsender.dao.helper;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;

import java.io.IOException;
import java.net.URLEncoder;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static ru.x5.motpsender.dao.helper.MotpTestConstants.*;

public class MotpParametrizedGetHelper implements MotpHelper {

    protected MediaType mediaType;
    private Resource mockedResponse;
    private String urlParameter;
    private String expectedUrl;
    private MockRestServiceServer mockRestServiceServer;

    public MotpParametrizedGetHelper(Resource mockedResponse, String urlParameter, String expectedUrl, MockRestServiceServer mockRestServiceServer) {
        this.mockedResponse = mockedResponse;
        this.urlParameter = urlParameter;
        this.expectedUrl = expectedUrl;
        this.mockRestServiceServer = mockRestServiceServer;
        this.mediaType =  MediaType.APPLICATION_JSON;
    }

    public void prepareTestData() throws IOException {
        String bodyExpected = IOUtils.toString(mockedResponse.getInputStream(), UTF8);
        String path = URLEncoder.encode(String.format(expectedUrl, urlParameter), UTF8);
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(path))
                .andExpect(method(HttpMethod.GET))
                .andExpect(MockRestRequestMatchers.header(AUTH_HEADER, TOKEN_HEADER))
                .andRespond(withSuccess(bodyExpected, mediaType));
    }
}
