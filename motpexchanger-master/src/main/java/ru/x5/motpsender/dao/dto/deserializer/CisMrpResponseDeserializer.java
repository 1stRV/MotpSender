package ru.x5.motpsender.dao.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ru.x5.motpsender.dao.dto.CisMrpDto;
import ru.x5.motpsender.dao.dto.CisMrpResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CisMrpResponseDeserializer extends JsonDeserializer<CisMrpResponse> {
    @Override
    public CisMrpResponse deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<CisMrpDto> cisMrpDtoList = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode jsonNode : node) {
                String gtin = jsonNode.get("gtin").asText();
                BigDecimal mrp = new BigDecimal(jsonNode.get("mrp").asText().replace(',', '.'));
                String productName = jsonNode.get("productName").asText();
                cisMrpDtoList.add(CisMrpDto.builder().gtin(gtin).mrp(mrp).productName(productName).build());
            }
        }
        return new CisMrpResponse(cisMrpDtoList);
    }
}

