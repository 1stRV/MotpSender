package ru.x5.motpsender.integration.producer;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.x5.motpsender.dao.MotpSender;
import ru.x5.motpsender.dao.dto.AggregatedCisResponse;
import ru.x5.motpsender.dao.dto.CisStatusRequest;
import ru.x5.motpsender.dao.dto.CisStatusResponse;
import ru.x5.motpsender.data.TestDataConstants;
import ru.x5.motpsender.integration.consumer.KafkaMotpConsumer;
import ru.x5.motpsender.integration.dto.KafkaAggregatedCisRequest;
import ru.x5.motpsender.integration.dto.KafkaCisStatusRequest;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ActiveProfiles("unit")
@TestPropertySource(locations = "classpath:application-unit.properties")
@EmbeddedKafka
@SpringBootTest
public class KafkaMotpProducerTest {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @MockBean
    private MotpSender motpSender;

    @Autowired
    KafkaMotpConsumer kafkaMotpConsumer;


    @Value("${kafka.topic.cis.aggregated.in}")
    private String aggregatedCisInTopic;
    @Value("${kafka.topic.cis.aggregated.out}")
    private String aggregatedCisOutTopic;
    @Value("${kafka.topic.cis.status.in}")
    private String statusCisInTopic;
    @Value("${kafka.topic.cis.status.out}")
    private String statusCisOutTopic;

    @ClassRule
    public static final EmbeddedKafkaRule  kafkaEmbeddedRule = new EmbeddedKafkaRule(
            1, true, 1, "aggregatedCisIn", "statusCisIn", "aggregatedCisOut", "statusCisOut");

    private EmbeddedKafkaBroker kafkaEmbedded;

    private final KafkaAggregatedCisRequest kafkaAggregatedCisRequest = new KafkaAggregatedCisRequest(TestDataConstants.CIS);

    private final CisStatusRequest cisStatusRequest = TestDataConstants.CIS_STATUS_REQUEST;
    private final AggregatedCisResponse aggregatedCisResponseExpected = TestDataConstants.AGGREGATED_CIS_RESPONSE;
    private final CisStatusResponse cisStatusResponse = TestDataConstants.CIS_STATUS_RESPONSE;
    private final KafkaCisStatusRequest kafkaCisStatusRequest = new KafkaCisStatusRequest(cisStatusRequest.getCis());


    @Before
    public void setup() {
        kafkaEmbedded = kafkaEmbeddedRule.getEmbeddedKafka();
    }

    @Ignore
    @Test
    public void aggregatedCis() {
        when(motpSender.getAggregatedCis(kafkaAggregatedCisRequest.getCis())).thenReturn(aggregatedCisResponseExpected);
        kafkaTemplate.send(aggregatedCisInTopic, kafkaAggregatedCisRequest);
        //todo: время получение нестабильно. Надо решить
        Awaitility.await().atMost(30, TimeUnit.SECONDS).until(kafkaMotpConsumer::isAggregatedCisRunned);
        verify(motpSender).getAggregatedCis(kafkaAggregatedCisRequest.getCis());
    }

    @Ignore
    @Test
    public void cisStatus() {
        when(motpSender.getCisStatus(cisStatusRequest)).thenReturn(cisStatusResponse);
        kafkaTemplate.send(statusCisInTopic, kafkaCisStatusRequest);
        //todo: время ответа нестабильно. Надо решить
        Awaitility.await().atMost(60, TimeUnit.SECONDS).until(kafkaMotpConsumer::isCisStatusRunned);
        verify(motpSender).getCisStatus(cisStatusRequest);
    }



}