package ru.x5.motpsender.dao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * идентификатор запроса order в ИС МОТП
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyCisPrepareResponse {
    @JsonProperty("uuid")
    private UUID responseUUID;
}
