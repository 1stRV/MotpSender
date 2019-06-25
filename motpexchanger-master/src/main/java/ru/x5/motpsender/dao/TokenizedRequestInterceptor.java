package ru.x5.motpsender.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import ru.x5.motpsender.dao.redis.MotpToken;
import ru.x5.motpsender.dao.redis.TokenRepository;

import java.io.IOException;
import java.util.Optional;

/**
 * Класс описывающий заголовок header для RestTemplate запросов.
 * Используется для добавления сведений о токене
 */
@Component
public class TokenizedRequestInterceptor implements ClientHttpRequestInterceptor {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private SessionInfo sessionInfo;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String userInn = sessionInfo.getUserInn();
        Optional<MotpToken> optionalMotpToken = tokenRepository.findById(userInn);
        optionalMotpToken.ifPresent(motpToken -> request.getHeaders().set("Authorization", "Bearer " + motpToken.getToken()));
        return execution.execute(request, body);
    }
}
