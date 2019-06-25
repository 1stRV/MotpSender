package ru.x5.motpsender.dao.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;


/**
 * 2.3.1 Запрос статуса кода маркировки
 */
public enum CisCodeStatus {
    EMITTED ("Эмитирован"),
    APPLIED ("Нанесён"),
    INTRODUCED ("Введён в оборот"),
    WRITTEN_OFF ("Выведен из оборота, списан"),
    WITHDRAWN ("Выведен из оборота, продан"),
    UNDEFINED ("Неопределен");

    private String description;

    CisCodeStatus(String description) {
        this.description = description;
    }

    @JsonCreator
    public static CisCodeStatus findByDescription(String value) {
        return CisCodeStatus.valueOf(value.toUpperCase());
    }
}
