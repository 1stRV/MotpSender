package ru.x5.motpsender.dao.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.x5.motpsender.dao.dto.deserializer.CisMrpResponseDeserializer;

import java.util.List;

/**
 * Информация о максимальной розничной цене табачной продукции, если она установлена
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = CisMrpResponseDeserializer.class)
public class CisMrpResponse {
    private List<CisMrpDto> cisMrpDtoList;
}
