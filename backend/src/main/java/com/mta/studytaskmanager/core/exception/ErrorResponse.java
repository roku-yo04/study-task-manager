package com.mta.studytaskmanager.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    // tiện lợi hơn list khi dò lỗi theo key:value thì vì loop từng phần như list.
    private Map<String,String> validationErrors; // key: field name, value: error message
}
