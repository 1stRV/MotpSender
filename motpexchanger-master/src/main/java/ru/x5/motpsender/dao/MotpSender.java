package ru.x5.motpsender.dao;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.x5.motpsender.dao.dto.*;
import ru.x5.motpsender.dao.dto.enums.DocumentType;
import ru.x5.motpsender.dao.dto.enums.RequestOrderStatus;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.MessageFormat;

/**
 * Класс для отправки запросов в ИС МОТП. Содержит основные методы запроса данных из раздела 2 API 1.2.2
 */
@Log4j2
@Service
public class MotpSender {

    private static final String INVALID_URI = "Invalid uri path: {0}";
    private static final String INVALID_ENCODING = "Invalid encoding: {0}";

    @Value("${motp.api.participant.status}")
    private String participantStatusPath;

    @Value("${motp.api.partners}")
    private String partnersPath;

    @Value("${motp.api.product}")
    private String productPath;

    @Value("${motp.api.product.card}")
    private String productCardPath;

    @Value("${motp.api.cis.status}")
    private String cisStatusPath;

    @Value("${motp.api.cis.mrp}")
    private String cisMrpPath;

    @Value("${motp.api.cis.aggregated}")
    private String cisAggregatedPath;

    @Value("${motp.api.cis.my.prepare}")
    private String myCisPreparePath;

    @Value("${motp.api.documents.body}")
    private String documentBodyPath;

    @Value("${motp.api.documents.prepare}")
    private String myDocumentsPreparePath;

    @Value("${motp.api.order.status}")
    private String requestOrderStatusPath;

    @Value("${motp.api.order.result}")
    private String requestOrderResultPath;

    @Value("${motp.encoding}")
    private String motpEncoding;

    @Autowired
    @Qualifier("authRestTemplate")
    RestTemplate authRestTemplate;


    /**
     * 2.1.1 Запрос статуса регистрации участника
     * @param participiantINN
     * @return информация о статусе регистрации в ИС МОТП, запрошенного ИНН
     */
    public GetParticipantStatusResponse getParticipiantStatus(String participiantINN) {
        try {
            String path = getUrlEncodedPath(participantStatusPath , participiantINN);
            return authRestTemplate.getForObject(new URI(path), GetParticipantStatusResponse.class);
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format(INVALID_URI, participantStatusPath));
            log.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 2.1.2 Запрос списка контрагентов участника
     * @return список, состоящий из ИНН контрагентов
     */
    public GetPartnersResponse getPartners() {
        try {
            return authRestTemplate.getForObject(new URI(partnersPath), GetPartnersResponse.class);
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format(INVALID_URI, partnersPath));
            log.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 2.2.1 Запрос списка продукции
     * @return список продукции, содержащие id – идентификатор продукта, gtin - международный товарный идентификатор и producerINN – ИНН производителя.
     */
    public GetProductsListResponse getProductsList() {
        try {
            return authRestTemplate.getForObject(new URI(productPath), GetProductsListResponse.class);
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format(INVALID_URI, productPath));
            log.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 2.2.2 Запрос карточки продукта
     * @param productId
     * @return информация о продукте и его производителе.
     */
    public ProductCardDto getProductCard(long productId) {
        try {
            String path = getUrlEncodedPath(productCardPath, String.valueOf(productId));
            return authRestTemplate.getForObject(new URI(path), ProductCardDto.class);
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format(INVALID_URI, productCardPath));
            log.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 2.3.1 Запрос статуса кода маркировки (включая владельца)
     * @param cisStatusRequest
     * @return список кодов маркировки их статус и владелец на момент запроса
     */
    public CisStatusResponse getCisStatus(CisStatusRequest cisStatusRequest) {
        try {
            return authRestTemplate.postForObject(new URI(cisStatusPath), cisStatusRequest, CisStatusResponse.class);
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format(INVALID_URI, cisStatusPath));
            log.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 2.3.2 Запрос МРЦ для кода маркировки. Если в запросе код маркировки является агрегат, то информация о цене будет
     * предоставлена минимальной единице товара, то есть пачки.
     * @param cis
     * @return информация о максимальной розничной цене табачной продукции, если она установлена
     */
    public CisMrpResponse getCisMrp(String cis) {
        String path = getUrlEncodedPath(cisMrpPath, cis);
        try {
            return authRestTemplate.getForObject(new URI(path), CisMrpResponse.class);
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format(INVALID_URI, path));
            log.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 2.3.3 Запрос данных об агрегации кодов маркировки
     * @param cis
     * @return информация о составе кода агрегата
     */
    public AggregatedCisResponse getAggregatedCis(String cis) {
        String path = getUrlEncodedPath(cisAggregatedPath, cis);
        try {
            return authRestTemplate.getForObject(new URI(path), AggregatedCisResponse.class);
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format(INVALID_URI, path));
            log.debug(e.getMessage());
        }
        return null;
    }

    //todo: realize if needed 2.3.4 Запрос цепочки движения кода маркировки


    /**
     * 2.3.5 Запрос списка кодов маркировки, принадлежащих участнику
     * 1 этап:  Формирование заказа на список кодов. С необязательным фильтром указания
     * типа упаковки. Короб- box, блок- block и пачка- pack.
     * @param myCisPrepareRequest
     * @return идентификатор запроса order в ИС МОТП
     */
    //todo: Need to specify result format. API not support JSON. Answer: result is String
    public MyCisPrepareResponse getMyCisPrepare(MyCisPrepareRequest myCisPrepareRequest) {
        try {
            return authRestTemplate.postForObject(new URI(myCisPreparePath), myCisPrepareRequest, MyCisPrepareResponse.class);
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format(INVALID_URI, myCisPreparePath));
            log.debug(e.getMessage());
        }
        return null;
    }


    /**
     * 2.4.2 Запрос списка документов
     * Формирование заказа на список документов. С необязательным фильтром
     * указания типа документа.
     * @param documentType - необязательный тип документа
     * @return
     */
    //todo: Need to specify result format. API not support JSON. Answer: result is String
    public String getMyDocumentsPrepare(DocumentType documentType) {
        try {
            return authRestTemplate.postForObject(new URI(myDocumentsPreparePath), documentType, String.class);
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format(INVALID_URI, myDocumentsPreparePath));
            log.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 2.4.2 Запрос списка документов
     * 3 этап: Запрос результата заказа.
     * @param documentId
     * @return ?
     */
    //todo: Need to specify result format and tests
    public String getDocumentBody(String documentId) {
        String path = getUrlEncodedPath(documentBodyPath, documentId);
        try {
            return authRestTemplate.getForObject(new URI(path), String.class);
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format(INVALID_URI, path));
            log.debug(e.getMessage());
        }
        return null;
    }

    /**
     * Метод применим к двум процессам
     * 2.3.5 Запрос списка кодов маркировки, принадлежащих участнику
     * 2.4.2 Запрос списка документов
     * 2 этап: Проверка статуса заказа.
     * @param orderUUID
     * @return В процессе выполнения- IN PROGRESS, задание выполнено- SUCCESS, при выполнении возникла ошибка- ERROR.
     */
    public RequestOrderStatus getRequestOrderStatus(String orderUUID) {
        String path = getUrlEncodedPath(requestOrderStatusPath, orderUUID);
        try {
            String result = authRestTemplate.getForObject(new URI(path), String.class);
            return result != null ? RequestOrderStatus.findByDescription(result) : null;
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format(INVALID_URI, path));
            log.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 2.3.5 Запрос списка кодов маркировки, принадлежащих участнику
     * 3 этап: Запрос результата заказа.
     * @param orderUUID
     * @return ?
     */
    //todo: Need to specify result format and tests
    public String getRequestOrderResult(String orderUUID) {
        String path = getUrlEncodedPath(requestOrderResultPath, orderUUID);
        try {
            return authRestTemplate.getForObject(new URI(path), String.class);
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format(INVALID_URI, path));
            log.debug(e.getMessage());
        }
        return null;
    }

    //todo: realize if needed 2.4.1 Запрос контента документа

    private String getUrlEncodedPath(String path, String parameter) {
        try {
            path = URLEncoder.encode(String.format(path, parameter), motpEncoding);
        } catch (UnsupportedEncodingException e) {
            log.error(MessageFormat.format(INVALID_ENCODING, motpEncoding));
            log.debug(e.getMessage());
        }
        return path;
    }
}
