package com.app.groupdeal.domain.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupMemberType {

    HOST("호스트"),
    MEMBER("참여자");

    private final String description;
}
