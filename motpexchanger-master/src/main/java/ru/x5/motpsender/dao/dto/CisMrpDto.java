package ru.x5.motpsender.dao.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Максимальная розничная цена продукции
 */
@Data
@Builder
public class CisMrpDto {
    private String gtin;
    private BigDecimal mrp;
    private String productName;
}
