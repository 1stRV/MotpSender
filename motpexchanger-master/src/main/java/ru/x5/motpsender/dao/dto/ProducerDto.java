package ru.x5.motpsender.dao.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация о производителе
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProducerDto {
    private String name;
    private String fullName;
    private String inn;
    private String kpp;
    private String fio;
    private String legalAddress;
}
