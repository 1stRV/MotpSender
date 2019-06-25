package ru.x5.motpsender.integration.consumer;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.adapter.ReplyHeadersConfigurer;
import org.springframework.kafka.support.KafkaHeaders;
import ru.x5.motpsender.dao.SessionInfo;
import ru.x5.motpsender.integration.dto.KafkaSessionInfo;

import java.util.Collections;
import java.util.Map;

@Configuration
@PropertySource("classpath:kafka.properties")
@Getter
public class KafkaConsumerProperties {

    @Value("${kafka.topic.cis.aggregated.in}")
    private String aggregatedCisInTopic;

    @Value("${kafka.topic.cis.aggregated.out}")
    private String aggregatedCisOutTopic;

    @Value("${kafka.topic.cis.status.in}")
    private String cisStatusInTopic;

    @Value("${kafka.topic.cis.status.out}")
    private String cisStatusOutTopic;

    @Value("${kafka.topic.token.in}")
    private String tokenIn;

    @Value("${kafka.topic.token.out}")
    private String tokenOut;

    @Value("${kafka.topic.products.in}")
    private String productsIn;

    @Value("${kafka.topic.products.out}")
    private String productsOut;

    @Value("${kafka.topic.repeat.in}")
    private String repeatIn;

    @Value("${kafka.repeat.group.id}")
    private String repeaterId;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaSessionInfo> kafkaListenerContainerFactory(
            SessionInfo sessionInfo, ConsumerFactory consumerFactory, KafkaTemplate kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, KafkaSessionInfo> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(kafkaTemplate);
        //добавление key во все @SendTo ответы
        factory.setReplyHeadersConfigurer(new ReplyHeadersConfigurer() {
            @Override
            public boolean shouldCopy(String headerName, Object headerValue) {
                return false;
            }

            @Override
            public Map<String, Object> additionalHeaders() {
                return Collections.singletonMap(KafkaHeaders.MESSAGE_KEY, sessionInfo.getGlobalUUID().toString());
            }
        });
        return factory;
    }

}
