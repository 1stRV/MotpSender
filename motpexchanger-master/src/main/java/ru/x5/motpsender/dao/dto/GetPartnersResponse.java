package ru.x5.motpsender.dao.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.x5.motpsender.dao.dto.deserializer.PartnersResponseDeserializer;

import java.util.List;

/**
 * список, состоящий из ИНН контрагентов
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = PartnersResponseDeserializer.class)
public class GetPartnersResponse {
    private List<String> partnersINN;
}
