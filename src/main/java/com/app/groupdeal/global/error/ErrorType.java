package com.app.groupdeal.global.error;

import lombok.Getter;

@Getter
public enum ErrorType {

    // 인증, 인가
    FORBIDDEN("A-001", "접근 권한이 없습니다."),
    INVALID_LOGIN_CREDENTIALS("A-002", "이메일 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED("A-003", "로그인이 필요합니다."),

    // 공통 (파라미터 사용)
    DUPLICATION("C-001", "중복된 %s이(가) 존재합니다."),
    NOT_FOUND("C-002", "%s을(를) 찾을 수 없습니다."),
    ALREADY_EXISTS("C-003", "이미 존재하는 %s입니다."),
    NOT_EXISTS("C-004", "존재하지 않는 %s입니다."),

    //그룹
    INVALID_MEETING_DATETIME("G-001", "거래 일시는 모집 마감 시간 이후여야 합니다."),


    // 기타 에러
    SERVER_ERROR("E-001", "알수 없는 에러가 발생하였습니다. 잠시 후에 시도해주세요")

    ;

    ErrorType(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String formatMessage(Object... args) {
        return String.format(errorMessage, args);
    }

    private String errorCode;
    private String errorMessage;
}
