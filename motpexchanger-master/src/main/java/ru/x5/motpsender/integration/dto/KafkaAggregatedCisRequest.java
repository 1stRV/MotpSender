package ru.x5.motpsender.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaAggregatedCisRequest extends KafkaSessionInfo {
    private String cis;
}
