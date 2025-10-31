package com.app.groupdeal.global.error;

import lombok.Getter;

@Getter
public enum ErrorType {

    // 인증, 인가
    FORBIDDEN("A-001", "접근 권한이 없습니다."),
    INVALID_LOGIN_CREDENTIALS("A-002", "이메일 또는 비밀번호가 올바르지 않습니다."),

    // 회원
    DUPLICATE_USER_EMAIL("U-001", "중복된 이메일이 존재합니다."),
    DUPLICATE_USER_NICKNAME("U-002", "중복된 닉네임이 존재합니다."),
    ALREADY_REGISTERED_USER("U-003", "이미 가입된 회원 입니다."),
    USER_NOT_EXISTS("U-004", "해당 회원은 존재하지 않습니다."),

    // 기타 에러
    SERVER_ERROR("E-001", "알수 없는 에러가 발생하였습니다. 잠시 후에 시도해주세요")

    ;

    ErrorType(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    private String errorCode;
    private String errorMessage;
}
