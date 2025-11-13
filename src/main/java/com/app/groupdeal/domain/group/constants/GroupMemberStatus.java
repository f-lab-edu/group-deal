package com.app.groupdeal.domain.group.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupMemberStatus {

    JOINED("참여 중"),
    CANCELLED("참여 취소");

    private final String description;
}
