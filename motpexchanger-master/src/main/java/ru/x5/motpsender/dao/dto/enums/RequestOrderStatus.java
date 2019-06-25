package ru.x5.motpsender.dao.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * В процессе выполнения- IN PROGRESS, задание выполнено- SUCCESS, при выполнении возникла ошибка- ERROR.
 */
public enum RequestOrderStatus {
    IN_PROGRESS ("IN PROGRESS"),
    SUCCESS ("SUCCESS"),
    ERROR ("ERROR");

    RequestOrderStatus(String description) {
    }

    @JsonCreator
    public static RequestOrderStatus findByDescription(String value) {
        return RequestOrderStatus.valueOf(value.toUpperCase());
    }
}
