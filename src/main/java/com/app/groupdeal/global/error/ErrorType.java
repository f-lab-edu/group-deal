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
    CANNOT_JOIN_OWN_GROUP("G-002", "자신이 생성한 그룹에는 참여할 수 없습니다."),
    INVALID_PARTICIPANT_COUNT("G-003", "참여자 수가 올바르지 않습니다"),
    GROUP_NOT_RECRUITING("G-004", "모집 중인 그룹이 아닙니다."),
    GROUP_FULL("G-005", "모집 인원이 초과되었습니다."),
    GROUP_DEADLINE_PASSED("G-006", "모집 기간이 종료되었습니다."),
    ALREADY_JOINED("G-007", "이미 참여한 그룹입니다."),
    CANNOT_DECREASE_PARTICIPANT("G-008", "참여자는 최소 1명 이상입니다."),
    CANNOT_LEAVE_CLOSED_GROUP("G-009", "마감된 그룹은 나갈 수 없습니다."),
    HOST_CANNOT_LEAVE("G-010", "호스트는 그룹을 나갈 수 없습니다."),
    ALREADY_LEFT("G-011", "이미 나간 그룹입니다"),
    INVALID_CLOSED_GROUP("G-012", "그룹을 모집 종료 할 수 없습니다."),




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
