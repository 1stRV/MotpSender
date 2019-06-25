package ru.x5.motpsender.dao;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.x5.motpsender.dao.dto.AuthResponse;
import ru.x5.motpsender.dao.dto.TokenRequest;
import ru.x5.motpsender.dao.dto.TokenResponse;
import ru.x5.motpsender.dao.redis.MotpToken;
import ru.x5.motpsender.dao.redis.TokenRepository;
import ru.x5.motpsender.dao.utilites.DataSigner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Класс содержит методы для авторизации в ИС МОТП
 * Предполагается использования в целях тестирования и отладки
 * Целевое решение - использование КриптоПро DSS
 */
@Log4j2
@Service
public class MotpAuth {

    @Value("${motp.api.auth}")
    private String authPath;

    @Value("${motp.api.sign}")
    private String signPath;

    @Value("${motp.auth.refresh.cron}")
    private String authRefreshCronExpression;

    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    @Autowired
    DataSigner dataSigner;

    @Autowired
    TokenRepository tokenRepository;

    /**
     * Получение данных для последующей авторизации
     *
     * @return данные для следующего подписания и авторизации
     */
    public AuthResponse getAuthCode() {
        try {
            return restTemplate.getForObject(new URI(authPath), AuthResponse.class);
        } catch (HttpServerErrorException e) {
            log.warn(e.getMessage());
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * Метод получения токена для работы с ИС МОТП
     *
     * @return bearer токен для дальнейшей работы
     */
    public TokenResponse getToken() {
        AuthResponse authResponse = getAuthCode();
        String signedData = dataSigner.signData(authResponse.getData());
        TokenRequest tokenRequest = TokenRequest.builder().uuid(authResponse.getUuid()).data(signedData).build();
        try {
            return restTemplate.postForObject(new URI(signPath), tokenRequest, TokenResponse.class);
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
            return null;
        }
    }


    /**
     * Метод для получения токена
     * В целях тестирования запрос токена происходит по cron
     * Запрос должен вызываться при возникновении ошибки 401 в authorizedRestTemplate
     */
    //todo: Доработать механизм
    //@PostConstruct
    //@Scheduled(cron = "${motp.auth.refresh.cron}")
    public void refreshToken() {

        log.debug("Start getting token");
        Iterable<MotpToken> motpTokenList = tokenRepository.findAll();
        for (MotpToken motpToken : motpTokenList) {
            if (motpToken.getLifetime() == null || motpToken.getTokenDate() == null ||
                    (motpToken.getTokenDate().getTime() + TimeUnit.MINUTES.toMillis(motpToken.getLifetime())) < new Date().getTime() - 300000) {
                try {
                    TokenResponse tokenResponse = getToken();
                    motpToken.setToken(tokenResponse.getToken()).setLifetime(tokenResponse.getLifetime()).setTokenDate(new Date());
                    tokenRepository.save(motpToken);
                } catch (Exception e) {
                    log.error(e.getMessage(), e.getCause());
                }
            }
        }
        log.debug("Stop getting token");
    }
}
