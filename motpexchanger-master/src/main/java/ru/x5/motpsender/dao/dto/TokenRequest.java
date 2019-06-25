package ru.x5.motpsender.dao.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


/**
* Подлписанные данные для авторизации в ИС МОТП из
* {@link ru.x5.motpsender.dao.dto.AuthResponse} подписывается с помощью ЭЦП
*/

@Data
@Builder
public class TokenRequest {
    private UUID uuid;
    private String data;
}
