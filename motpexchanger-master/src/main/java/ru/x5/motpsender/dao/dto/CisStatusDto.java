package ru.x5.motpsender.dao.dto;

import lombok.Builder;
import lombok.Data;
import ru.x5.motpsender.dao.dto.enums.CisCodeStatus;

/**
 * Код маркировки, статус и владелец на момент запроса
 */
@Data
@Builder
public class CisStatusDto {
    private String cis;
    private CisCodeStatus status;
    private String ownerInn;
}
