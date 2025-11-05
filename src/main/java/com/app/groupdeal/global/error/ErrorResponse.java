package com.app.groupdeal.global.error;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private String errorCode;
    private String errorMessage;

    public static ErrorResponse of(String errorCode, String errorMessage) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    public static ErrorResponse of(ErrorType errorType) {
        return ErrorResponse.builder()
                .errorCode(errorType.getErrorCode())
                .errorMessage(errorType.getErrorMessage())
                .build();
    }
}
