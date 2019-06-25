package ru.x5.motpsender.dao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * список продукции, содержащие id – идентификатор продукта, gtin - международный товарный идентификатор и producerINN – ИНН производителя.
 * total - количество продуктов в списке
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetProductsListResponse {
    @JsonProperty("results")
    private List<ProductDto> productDtoList;
    private Integer total;
    private Integer errorCode;
    private Boolean last;

}
