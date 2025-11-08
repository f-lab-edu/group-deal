package com.app.groupdeal.domain.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupStatus {

    RECRUITING("모집 중"),
    CLOSED("모집 마감"),
    COMPLETED("거래 완료"),
    CANCELLED("거래 취소");

    private final String description;
}
