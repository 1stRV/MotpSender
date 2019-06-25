package ru.x5.motpsender.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.x5.motpsender.MotpApplicationTests;
import ru.x5.motpsender.dao.dto.*;
import ru.x5.motpsender.dao.dto.enums.DocumentType;
import ru.x5.motpsender.dao.dto.enums.PackageType;
import ru.x5.motpsender.dao.dto.enums.RequestOrderStatus;
import ru.x5.motpsender.dao.helper.*;
import ru.x5.motpsender.dao.redis.MotpToken;
import ru.x5.motpsender.dao.redis.TokenRepository;
import ru.x5.motpsender.data.TestDataConstants;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class MotpSenderTest extends MotpApplicationTests {

    @Autowired
    MotpSender motpSender;

    @Autowired
    @Qualifier("authRestTemplate")
    private RestTemplate authRestTemplate;

    @MockBean
    TokenRepository tokenRepository;

    @MockBean
    private SessionInfo sessionInfo;

    private MockRestServiceServer mockServer;

    private GetProductsListResponse productsListResponseExpected;

    private ProductCardDto productsCardResponseExpected;

    private CisStatusRequest cisStatusRequest;

    private CisStatusResponse cisStatusResponseExpected;

    private CisMrpResponse cisMrpResponseExpected;

    private AggregatedCisResponse aggregatedCisResponseExpected;

    private MyCisPrepareRequest myCisPrepareRequest;

    private MyCisPrepareResponse myCisPrepareResponseExpected;

    private GetParticipantStatusResponse participantStatusResponseExpected;

    private GetPartnersResponse partnersResponseExpected;



    @Value("${motp.api.participant.status}")
    private String participantStatusExpectedUrl;

    @Value("${motp.api.partners}")
    private String partnersExpectedUrl;

    @Value("${motp.api.product.card}")
    private String productCardExpectedUrl;

    @Value("${motp.api.product}")
    private String productExpectedUrl;

    @Value("${motp.api.cis.status}")
    private String cisStatusExpectedUrl;

    @Value("${motp.api.cis.mrp}")
    private String cisMrpExpectedUrl;

    @Value("${motp.api.cis.aggregated}")
    private String cisAggregatedExpectedUrl;

    @Value("${motp.api.cis.my.prepare}")
    private String myCisPrepareExpectedUrl;

    @Value("${motp.api.order.status}")
    private String requestOrderStatusExpectedUrl;

    @Value("${motp.api.documents.prepare}")
    private String myDocumentsExpectedUrl;

    @Value("classpath:json/get-participiant-status-response.json")
    private Resource participantStatusResponse;

    @Value("classpath:json/get-partners-response.json")
    private Resource partnersResponse;

    @Value("classpath:json/get-product-list-response.json")
    private Resource productListResponse;

    @Value("classpath:json/get-product-card-response.json")
    private Resource productCardResponse;

    @Value("classpath:json/post-cis-status-response.json")
    private Resource cisStatusResponse;

    @Value("classpath:json/post-cis-status-request.json")
    private Resource cisStatusRequestExpected;

    @Value("classpath:json/get-cis-mrp-response.json")
    private Resource cisMrpResponse;

    @Value("classpath:json/get-aggregated-cis-response.json")
    private Resource aggregatedCisResponse;

    @Value("classpath:json/post-my-cis-prepare-response.json")
    private Resource myCisPrepareResponse;

    @Value("classpath:json/post-my-cis-prepare-request.json")
    private Resource myCisPrepareRequestExpected;

    @Value("classpath:json/get-request-order-status.txt")
    private Resource requestOrderSuccessResponseExpected;

    @Value("classpath:json/post-my-documents-prepare-request.json")
    private Resource myDocumentsPrepareRequestExpected;

    @Value("classpath:json/post-my-documents-prepare-response.txt")
    private Resource myDocumentsPrepareResponse;

    private static final Long PRODUCT_ID = 3L;
    private static final String CIS = "(01)04606203085835(21)<>Z4Qp>";
    private static final String INN = "8613005161";
    private static final String INN2 = "7809008119";
    private static final String ORDER_UUID = "f6a9662a-f7d0-444e-8ae0-3fd29d860a31";
    private static final String ORDER_UUID_STR = "{f6a9662a-f7d0-444e-8ae0-3fd29d860a31}";


    @Before
    public void init() {
        mockServer = MockRestServiceServer.bindTo(authRestTemplate).build();

        prepareProductsListResponseExpected();

        prepareProductsCardResponseExpected();

        prepareCisStatusRequestExpected();

        prepareCisStatusResponseExpected();

        prepareCisMrpResponseExpected();

        prepareAggregatedCisResponseExpected();

        prepareMyCisPrepareRequestExpected();

        prepareMyCisPrepareResponseExpected();

        prepareParticipantStatusExpexted();

        preparePartnersResponseExpexted();

        when(sessionInfo.getUserInn()).thenReturn(INN);
        when(tokenRepository.findById(anyString())).thenReturn(Optional.of(new MotpToken(INN, "token",1000, new Date())));
    }

    private void preparePartnersResponseExpexted() {
        partnersResponseExpected = GetPartnersResponse
                .builder()
                .partnersINN(Arrays.asList("7718239655","5032212790","7804064663","7707329152","7727707701","7725519413","7813411433","7731369928",INN))
                .build();
    }

    private void prepareParticipantStatusExpexted() {
        participantStatusResponseExpected = GetParticipantStatusResponse.builder().status("Зарегистрирован").build();
    }

    private void prepareMyCisPrepareResponseExpected() {
        myCisPrepareResponseExpected = MyCisPrepareResponse.builder().responseUUID(UUID.fromString(ORDER_UUID)).build();
    }

    private void prepareMyCisPrepareRequestExpected() {
        myCisPrepareRequest = MyCisPrepareRequest.builder().packageType(PackageType.PACK).build();
    }

    private void prepareAggregatedCisResponseExpected() {
        aggregatedCisResponseExpected = TestDataConstants.AGGREGATED_CIS_RESPONSE;
    }

    private void prepareCisMrpResponseExpected() {
        CisMrpDto cisMrpDto = CisMrpDto.builder()
                .gtin("00000046186195")
                .mrp(new BigDecimal("105,00" .replace(',', '.')))
                .productName("сигареты с фильтром Winston Compact Plus Impulse, MРЦ 6")
                .build();
        cisMrpResponseExpected = CisMrpResponse.builder()
                .cisMrpDtoList(new ArrayList<>(Arrays.asList(cisMrpDto)))
                .build();
    }

    private void prepareCisStatusRequestExpected() {
        cisStatusRequest = TestDataConstants.CIS_STATUS_REQUEST;
    }

    private void prepareProductsListResponseExpected() {
        ProductDto productDto1 = ProductDto.builder().id(3L).gtin("00000046057389").producerINN(INN).build();
        ProductDto productDto2 = ProductDto.builder().id(29L).gtin("00000046152770").producerINN(INN).build();
        ProductDto productDto3 = ProductDto.builder().id(31L).gtin("00000046205391").producerINN(INN).build();
        productsListResponseExpected = GetProductsListResponse.builder()
                .productDtoList(new ArrayList<>(Arrays.asList(productDto1, productDto2, productDto3)))
                .errorCode(null)
                .total(3)
                .last(true)
                .build();
    }

    private void prepareProductsCardResponseExpected() {
        ProducerDto producerDto = ProducerDto.builder()
                .name("АО \"БАТ-СПБ\"")
                .fullName("АКЦИОНЕРНОЕ ОБЩЕСТВО \"БРИТИШ АМЕРИКАН ТОБАККО-СПБ\"")
                .inn(INN2)
                .kpp("781401001")
                .fio("Де Врис Хендрик Бернард")
                .legalAddress("197229, САНКТ-ПЕТЕРБУРГ Г, 3-Я КОННАЯ ЛАХТА УЛ, 38")
                .build();

        productsCardResponseExpected = ProductCardDto.builder()
                .brand("нет")
                .gs1Synced("synced")
                .gtin("04600266011817")
                .id(3L)
                .innerUnitCount(20)
                .name("Сигареты с фильтром \"Rothmans Аэро Блю\" QR code")
                .packageType(PackageType.PACK)
                .producers(new ArrayList<>(Arrays.asList(producerDto)))
                .shortName("Сигареты с фильтром \"Rothmans Аэро Блю\" QR code")
                .build();
    }

    private void prepareCisStatusResponseExpected() {
        cisStatusResponseExpected = TestDataConstants.CIS_STATUS_RESPONSE;
    }

    @Test
    public void getParticipantStatus() throws IOException {
        MotpHelper motpPostHelper = new MotpParametrizedGetHelper(participantStatusResponse, INN, participantStatusExpectedUrl, mockServer);
        motpPostHelper.prepareTestData();
        GetParticipantStatusResponse participantStatusResponseActual = motpSender.getParticipiantStatus(INN);
        assertEquals(participantStatusResponseExpected, participantStatusResponseActual);

    }

    @Test
    public void getPartners() throws IOException {
        MotpHelper motpHelper = new MotpSimpleGetHelper(partnersResponse, partnersExpectedUrl, mockServer);
        motpHelper.prepareTestData();
        GetPartnersResponse partnersResponseActual = motpSender.getPartners();
        assertEquals(partnersResponseExpected, partnersResponseActual);
    }

    @Test
    public void getProductsList() throws IOException {
        MotpHelper motpHelper = new MotpSimpleGetHelper(productListResponse, productExpectedUrl, mockServer);
        motpHelper.prepareTestData();
        GetProductsListResponse productsListResponse = motpSender.getProductsList();
        assertEquals(productsListResponseExpected, productsListResponse);
    }

    @Test
    public void getProductCard() throws IOException {
        MotpHelper motpHelper = new MotpParametrizedGetHelper(productCardResponse, String.valueOf(PRODUCT_ID), productCardExpectedUrl, mockServer);
        motpHelper.prepareTestData();
        ProductCardDto productsCardResponse = motpSender.getProductCard(PRODUCT_ID);
        assertEquals(productsCardResponseExpected, productsCardResponse);
    }


    @Test
    public void getCisStatus() throws IOException {
        MotpHelper motpHelper = new MotpPostHelper(cisStatusResponse, cisStatusRequestExpected, cisStatusExpectedUrl, mockServer);
        motpHelper.prepareTestData();
        CisStatusResponse cisStatusResponseActual = motpSender.getCisStatus(cisStatusRequest);
        assertEquals(cisStatusResponseExpected, cisStatusResponseActual);
    }

    @Test
    public void getCisMrpSingle() throws IOException {
        MotpHelper motpHelper = new MotpParametrizedGetHelper(cisMrpResponse, CIS, cisMrpExpectedUrl, mockServer);
        motpHelper.prepareTestData();
        CisMrpResponse cisMrpResponseActual = motpSender.getCisMrp(CIS);
        assertEquals(cisMrpResponseExpected, cisMrpResponseActual);
    }

    @Test
    public void getAggregatedCis() throws IOException {
        MotpHelper motpHelper = new MotpParametrizedGetHelper(aggregatedCisResponse, CIS, cisAggregatedExpectedUrl, mockServer);
        motpHelper.prepareTestData();
        AggregatedCisResponse aggregatedCisResponseActual = motpSender.getAggregatedCis(CIS);
        assertEquals(aggregatedCisResponseExpected, aggregatedCisResponseActual);
    }

    @Test
    public void getMyCisPrepare() throws IOException {
        MotpHelper motpHelper = new MotpPostHelper(myCisPrepareResponse, myCisPrepareRequestExpected, myCisPrepareExpectedUrl, mockServer);
        motpHelper.prepareTestData();
        MyCisPrepareResponse myCisPrepareResponseActual = motpSender.getMyCisPrepare(myCisPrepareRequest);
        assertEquals(myCisPrepareResponseExpected, myCisPrepareResponseActual);
    }

    @Test
    public void getMyCisPrepareWithNull() throws IOException {
        MotpHelper motpHelper = new MotpPostHelper(myCisPrepareResponse, null, myCisPrepareExpectedUrl, mockServer);
        motpHelper.prepareTestData();
        MyCisPrepareResponse myCisPrepareResponseActual = motpSender.getMyCisPrepare(null);
        assertEquals(myCisPrepareResponseExpected, myCisPrepareResponseActual);
    }

    @Test
    public void getRequestOrderStatusSuccess() throws IOException {
        MotpHelper motpHelper = new MotpParametrizedGetTextHelper(requestOrderSuccessResponseExpected, ORDER_UUID, requestOrderStatusExpectedUrl, mockServer);
        motpHelper.prepareTestData();
        RequestOrderStatus requestOrderStatus = motpSender.getRequestOrderStatus(ORDER_UUID);
        assertEquals(RequestOrderStatus.SUCCESS, requestOrderStatus);
    }

    @Test
    public void getMyDocumentsPrepare() throws IOException {
        MotpHelper motpHelper = new MotpPostHelper(myDocumentsPrepareResponse, myDocumentsPrepareRequestExpected, myDocumentsExpectedUrl, mockServer);
        motpHelper.prepareTestData();
        String myDocumentsPrepare = motpSender.getMyDocumentsPrepare(DocumentType.RECEIPT);
        assertEquals(ORDER_UUID_STR, myDocumentsPrepare);
    }
}