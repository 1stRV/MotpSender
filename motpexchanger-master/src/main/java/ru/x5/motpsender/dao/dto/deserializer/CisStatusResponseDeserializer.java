package ru.x5.motpsender.dao.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ru.x5.motpsender.dao.dto.CisStatusDto;
import ru.x5.motpsender.dao.dto.CisStatusResponse;
import ru.x5.motpsender.dao.dto.enums.CisCodeStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CisStatusResponseDeserializer extends JsonDeserializer<CisStatusResponse> {
    @Override
    public CisStatusResponse deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<CisStatusDto> cisStatusDtoList = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode jsonNode : node) {
                String cis = jsonNode.get("cis").asText();
                CisCodeStatus cisCodeStatus = CisCodeStatus.findByDescription(jsonNode.get("status").asText());
                String ownerInn = jsonNode.get("ownerInn").asText();
                cisStatusDtoList.add(CisStatusDto.builder().cis(cis).ownerInn(ownerInn).status(cisCodeStatus).build());
            }
        }
        return new CisStatusResponse(cisStatusDtoList);
    }
}

