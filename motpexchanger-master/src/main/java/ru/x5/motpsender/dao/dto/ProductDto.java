package ru.x5.motpsender.dao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Экземпляр продукции, содержащий id – идентификатор продукта, gtin - международный товарный идентификатор и producerINN – ИНН производителя.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String gtin;
    private String producerINN;

}
