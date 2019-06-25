package ru.x5.motpsender.dao.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Токен авторизации ИС МОТП
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String token;
    private Integer lifetime;
}
