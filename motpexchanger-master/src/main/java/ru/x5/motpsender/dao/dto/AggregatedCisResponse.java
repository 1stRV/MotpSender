package ru.x5.motpsender.dao.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.x5.motpsender.dao.dto.deserializer.AggregatedCisResponseDeserializer;

import java.util.List;

/**
 * Информация о составе кода агрегата
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = AggregatedCisResponseDeserializer.class)
public class AggregatedCisResponse {
    private String rootCis;
    private List aggregatedCis;
}
