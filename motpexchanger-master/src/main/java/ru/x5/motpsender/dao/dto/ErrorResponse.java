package ru.x5.motpsender.dao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ErrorResponse {
    private Date timestamp;
    private String status;
    private String message;
    private String path;
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
}
