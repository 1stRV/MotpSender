package ru.x5.motpsender.dao.utilites;

/**
 * Интерфейс подписи строки данных с помощью ЭЦП.
 * Для совместимости с разными решениями по подписанию данных при получении токена из ИС МОТП
 */
public interface DataSigner {
    String signData(String data);
}
