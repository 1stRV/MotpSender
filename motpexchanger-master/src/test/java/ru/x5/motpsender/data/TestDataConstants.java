package ru.x5.motpsender.data;

import ru.x5.motpsender.dao.dto.AggregatedCisResponse;
import ru.x5.motpsender.dao.dto.CisStatusDto;
import ru.x5.motpsender.dao.dto.CisStatusRequest;
import ru.x5.motpsender.dao.dto.CisStatusResponse;
import ru.x5.motpsender.dao.dto.enums.CisCodeStatus;

import java.util.ArrayList;
import java.util.Arrays;

public class TestDataConstants {
    public static final String INN2 = "7809008119";
    public static final String CIS = "(01)04606203085835(21)<>Z4Qp>";
    public static final CisStatusRequest CIS_STATUS_REQUEST = CisStatusRequest.builder()
            .cis(new ArrayList<>(Arrays.asList("00000046210654wru;g-*", "00000046210654pPM==mi", "000000462106543,gE!ae", "00000046210654dbh7z=H")))
            .build();
    public static final AggregatedCisResponse AGGREGATED_CIS_RESPONSE = AggregatedCisResponse.builder()
            .rootCis(CIS)
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

    public static final CisStatusResponse CIS_STATUS_RESPONSE = CisStatusResponse.builder()
            .cisStatusDtoList(new ArrayList<>(Arrays.asList(CisStatusDto.builder()
                    .cis("00000046210654pPM==mi")
                    .status(CisCodeStatus.WITHDRAWN)
                    .ownerInn(INN2)
                    .build(), CisStatusDto.builder()
                    .cis("000000462106543,gE!ae")
                    .status(CisCodeStatus.APPLIED)
                    .ownerInn(INN2)
                    .build(), CisStatusDto.builder()
                    .cis("00000046210654dbh7z=H")
                    .status(CisCodeStatus.WITHDRAWN)
                    .ownerInn(INN2)
                    .build())))
                    .build();


    private TestDataConstants() {

    }
}
