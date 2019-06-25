package ru.x5.motpsender.dao.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.x5.motpsender.dao.dto.enums.PackageType;

/**
 * Короб- box, блок- block и пачка- pack.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyCisPrepareRequest {
    @JsonValue
    private PackageType packageType;
}
