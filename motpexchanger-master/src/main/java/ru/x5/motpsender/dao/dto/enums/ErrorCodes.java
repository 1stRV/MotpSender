package ru.x5.motpsender.dao.dto.enums;

public enum ErrorCodes {

    ERROR_UNKNOWN(-1,"UNKNOWN UNDEFINED "),
    ERROR0(0,"Обработка документа прошла успешно "),
    ERROR1(1,"Отправлено в обработку "),
    ERROR4(4,"Документ #documentId# зарегистрирован ранее "),
    ERROR5(5,"В данном документе #documentId# данные по табачной продукции отсутствуют "),
    ERROR6(6,"Для данного корректировочного документа отсутствует исходный документ #invoiceId#, #invoiceDate# "),
    ERROR10(10,"Продавец #vendor# не идентифицирован (не зарегистрирован) "),
    ERROR11(11,"Продавец #vendor# не идентифицирован (не зарегистрирован) на указанную дату #date# "),
    ERROR12(12,"Покупатель #customer# не идентифицирован (не зарегистрирован) "),
    ERROR13(13,"Участник #participant# не идентифицирован (не зарегистрирован) "),
    ERROR21(21,"КиЗ #cis# не зарегистрирован(ы) в системе "),
    ERROR22(22,"КиЗ #cis# не найден(ы) "),
    ERROR23(23,"У участника #participant# нет полномочий на выполнение операции с КиЗ #cis#"),
    ERROR24(24,"Статус КиЗ #cis# не соответствует выполняемой операции"),
    ERROR43(43,"Ошибка отправки на обработку "),
    ERROR44(44,"Файл description.xml не прошел валидацию "),
    ERROR45(45,"Цифровая подпись некорректна "),
    ERROR46(46,"Содержание или имя документа некорректно "),
    ERROR47(47,"Ни один из файлов не соответствует протоколу "),
    ERROR48(48,"Некорректное содержание файлов в логическом сообщении "),
    ERROR49(49,"Сообщение об ошибке дат "),
    ERROR50(50,"XSD Validation returning FALSE. Ошибка проверке по XSD-схеме ");

    private final int code;
    private final String description;

    ErrorCodes(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }
}
