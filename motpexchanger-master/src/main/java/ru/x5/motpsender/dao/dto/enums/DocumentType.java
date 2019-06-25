package ru.x5.motpsender.dao.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

//todo: API contains only one document type. Need to clarify
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum DocumentType {
    RECEIPT ("receipt");

    private String documentType;

    DocumentType(String documentType) {
        this.documentType = documentType;
    }

    @JsonCreator
    public static DocumentType findByDescription(String value) {
        return DocumentType.valueOf(value.toUpperCase());
    }
}
