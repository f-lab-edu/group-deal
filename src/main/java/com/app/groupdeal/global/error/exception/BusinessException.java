package com.app.groupdeal.global.error.exception;

import com.app.groupdeal.global.error.ErrorType;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private ErrorType errorType;

    public BusinessException(ErrorType errorType) {
        super(errorType.getErrorMessage());
        this.errorType = errorType;
    }
}
