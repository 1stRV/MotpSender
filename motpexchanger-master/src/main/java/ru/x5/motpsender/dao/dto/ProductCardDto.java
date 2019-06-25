package ru.x5.motpsender.dao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.x5.motpsender.dao.dto.enums.PackageType;

import java.util.List;

/**
 * информация о продукте и его производителе.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCardDto {
    private Long id;
    private String name;
    private String shortName;
    private String gtin;
    private String brand;
    private PackageType packageType;
    private Integer innerUnitCount;
    private List<ProducerDto> producers;
    private String gs1Synced;

}
