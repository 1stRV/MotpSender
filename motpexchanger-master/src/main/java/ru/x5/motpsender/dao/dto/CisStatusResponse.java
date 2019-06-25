package ru.x5.motpsender.dao.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.x5.motpsender.dao.dto.deserializer.CisStatusResponseDeserializer;

import java.util.List;

/**
 * список кодов маркировки их статус и владелец на момент запроса
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = CisStatusResponseDeserializer.class)
public class CisStatusResponse {
    private List<CisStatusDto> cisStatusDtoList;
}
