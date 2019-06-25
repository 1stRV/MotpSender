package ru.x5.motpsender.dao.helper;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

public class MotpParametrizedGetTextHelper extends MotpParametrizedGetHelper {

    public MotpParametrizedGetTextHelper(Resource mockedResponse, String urlParameter, String expectedUrl, MockRestServiceServer mockRestServiceServer) {
        super(mockedResponse, urlParameter, expectedUrl, mockRestServiceServer);
        this.mediaType = MediaType.TEXT_PLAIN;
    }
}