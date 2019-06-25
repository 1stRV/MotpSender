package ru.x5.motpsender.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaCisStatusRequest extends KafkaSessionInfo {
    private List<String> cis;
}
