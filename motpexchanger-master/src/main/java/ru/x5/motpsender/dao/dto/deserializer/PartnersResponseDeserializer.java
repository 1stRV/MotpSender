package ru.x5.motpsender.dao.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ru.x5.motpsender.dao.dto.GetPartnersResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PartnersResponseDeserializer extends JsonDeserializer<GetPartnersResponse> {
    @Override
    public GetPartnersResponse deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<String> partnersList = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode jsonNode : node) {
                partnersList.add(jsonNode.asText());
            }
        }
        return new GetPartnersResponse(partnersList);
    }
}

