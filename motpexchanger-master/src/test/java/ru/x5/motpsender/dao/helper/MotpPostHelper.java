package ru.x5.motpsender.dao.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;

import java.io.IOException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static ru.x5.motpsender.dao.helper.MotpTestConstants.*;

@Builder
@AllArgsConstructor
public class MotpPostHelper implements MotpHelper {

    private Resource mockedResponse;
    private Resource expectedRequest;
    private String expectedUrl;
    private MockRestServiceServer mockRestServiceServer;

    @Override
    public void prepareTestData() throws IOException {
        String responseBody = IOUtils.toString(mockedResponse.getInputStream(), UTF8);
        String requestExpected = "";
        if (expectedRequest != null) {
            requestExpected = IOUtils.toString(expectedRequest.getInputStream(), UTF8);
        }
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(expectedUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(requestExpected))
                .andExpect(MockRestRequestMatchers.header(AUTH_HEADER, TOKEN_HEADER))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
    }
}
