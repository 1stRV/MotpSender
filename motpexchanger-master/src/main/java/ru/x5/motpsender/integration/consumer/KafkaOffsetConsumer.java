package ru.x5.motpsender.integration.consumer;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.x5.motpsender.integration.dto.KafkaRepeatRequest;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Cервис для повторной отправки сообщения в очередь Kafka. Отправляется по offset из очереди с повторением INN, key и value
 */

@Log4j2
@Service
@EnableKafka
public class KafkaOffsetConsumer {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${kafka.poll.duration}")
    private long pollDuration;

    @Value("#{kafkaConsumerProperties.getRepeaterId()}")
    private String repeaterGroupId;

    @KafkaListener(topics = "#{kafkaConsumerProperties.getRepeatIn()}", groupId = "#{kafkaConsumerProperties.getRepeaterId()}")
    public void repeatMessage(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String keyUUID, KafkaRepeatRequest message) {
        Map<String, Object> repeaterProperties = kafkaProperties.buildConsumerProperties();
        repeaterProperties.replace("group.id", repeaterGroupId);
        try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(repeaterProperties)) {
            TopicPartition topicPartition = new TopicPartition(message.getTopic(), message.getPartition());
            List<TopicPartition> topicPartitionList = Arrays.asList(topicPartition);
            kafkaConsumer.assign(topicPartitionList);
            kafkaConsumer.seek(topicPartition, message.getOffset());
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(pollDuration));
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                if (consumerRecord.offset() == message.getOffset() && (message.getKey() == null || keyUUID.equals(consumerRecord.key())))
                    kafkaTemplate.send(consumerRecord.topic(), consumerRecord.key(), consumerRecord.value());
            }
        }
    }
}
