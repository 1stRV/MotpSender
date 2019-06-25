package ru.x5.motpsender.integration.consumer;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import ru.x5.motpsender.dao.MotpSender;
import ru.x5.motpsender.dao.SessionInfo;
import ru.x5.motpsender.dao.dto.AggregatedCisResponse;
import ru.x5.motpsender.dao.dto.CisStatusRequest;
import ru.x5.motpsender.dao.dto.CisStatusResponse;
import ru.x5.motpsender.dao.dto.GetProductsListResponse;
import ru.x5.motpsender.dao.redis.MotpToken;
import ru.x5.motpsender.dao.redis.TokenRepository;
import ru.x5.motpsender.integration.dto.KafkaAggregatedCisRequest;
import ru.x5.motpsender.integration.dto.KafkaCisStatusRequest;
import ru.x5.motpsender.integration.dto.KafkaSessionInfo;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Класс получает сообщения от внешних систем через kafka, обрабатывает и направляет ответ
 * Класс включает в себя бизнес-логику сервиса. Возможно, это надо изменить для добавления синхронных интерфейсов
 */
@Log4j2
@Service
public class KafkaMotpConsumer {

    @Autowired
    private MotpSender motpSender;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private SessionInfo sessionInfo;

    /**
     * Введен для синхронизации при тестировании
     */
    @Getter
    volatile boolean aggregatedCisRunned;

    /**
     * Введен для синхронизации при тестировании
     */
    @Getter
    volatile boolean cisStatusRunned;

    /**
     * Введен для синхронизации при тестировании
     */
    @Getter
    volatile boolean productsRunned;

    @KafkaListener(topics = "#{kafkaConsumerProperties.getAggregatedCisInTopic()}")
    @SendTo("#{kafkaConsumerProperties.getAggregatedCisOutTopic()}")
    public AggregatedCisResponse aggregatedCis(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String keyUUID, KafkaAggregatedCisRequest message) {
        sessionInfo.setGlobalUUID(keyUUID);
        sessionInfo.setUserInn(message.getUserInn());
        //todo: Убрать заглушку когда МОТП наконец заработает
        //AggregatedCisResponse aggregatedCisResponse = motpSender.getAggregatedCis(message.getCis());
        AggregatedCisResponse aggregatedCisResponse = AggregatedCisResponse.builder()
                .rootCis("rootCis00000011111111rx9")
                .aggregatedCis(new ArrayList<>(Arrays.asList("00000011111111rx9>=90",
                        "00000011111111*di*X>&",
                        "00000011111111eiX,_09",
                        "00000011111111fP7OR2_",
                        "000000111111111%%oAB5",
                        "00000011111111XSD&%7P",
                        "00000011111111>1bfMy>",
                        "00000011111111ExFXW,W",
                        "00000011111111dmA-FM1",
                        "00000011111111-o,tb<n")))
                .build();
        aggregatedCisRunned = true;
        return aggregatedCisResponse;

    }

    @KafkaListener(topics = "#{kafkaConsumerProperties.getCisStatusInTopic()}")
    @SendTo("#{kafkaConsumerProperties.getCisStatusOutTopic()}")
    public CisStatusResponse cisStatus(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String keyUUID, KafkaCisStatusRequest message) {
        sessionInfo.setGlobalUUID(keyUUID);
        sessionInfo.setUserInn(message.getUserInn());
        CisStatusRequest cisStatusRequest = modelMapper.map(message, CisStatusRequest.class);
        sessionInfo.setUserInn(message.getUserInn());
        CisStatusResponse cisStatus = motpSender.getCisStatus(cisStatusRequest);
        cisStatusRunned = true;
        return cisStatus;
    }

    @KafkaListener(topics = "#{kafkaConsumerProperties.getTokenIn()}")
    @SendTo("#{kafkaConsumerProperties.getTokenOut()}")
    public MotpToken cisStatus(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String keyUUID, MotpToken message) {
        sessionInfo.setGlobalUUID(keyUUID);
        sessionInfo.setUserInn(message.getInn());
        tokenRepository.save(message);
        Optional<MotpToken> optionalMotpToken = tokenRepository.findById(message.getInn());
        if (optionalMotpToken.isPresent()) {
            return optionalMotpToken.get();
        } else {
            log.error(MessageFormat.format("Не найден токен для ИНН {0}", message.getInn()));
            return null;
        }

    }

    @KafkaListener(topics = "#{kafkaConsumerProperties.getProductsIn()}")
    @SendTo("#{kafkaConsumerProperties.getProductsOut()}")
    public GetProductsListResponse products(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String keyUUID, KafkaSessionInfo message) {
        sessionInfo.setGlobalUUID(keyUUID);
        sessionInfo.setUserInn(message.getUserInn());
        GetProductsListResponse getProductsListResponse = motpSender.getProductsList();
        productsRunned = true;
        return getProductsListResponse;
    }

    @KafkaListener(topics = "testTopic")
    public void find(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String keyUUID, String message) {
        log.debug("Receive msg " + keyUUID + " lenght: " + message.length());
    }


}
