package com.app.groupdeal.domain.group.model;

import com.app.groupdeal.domain.common.BaseDomain;
import com.app.groupdeal.domain.group.constants.GroupMemberStatus;
import com.app.groupdeal.domain.group.constants.GroupMemberType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GroupMember extends BaseDomain {

    private Long groupMemberId;
    private Long groupId;
    private Long userId;
    private GroupMemberType groupMemberType;
    private GroupMemberStatus groupMemberStatus;
    private LocalDateTime joinedAt;

    @Builder
    public GroupMember(Long groupMemberId, Long groupId, Long userId, GroupMemberType groupMemberType,
                       GroupMemberStatus groupMemberStatus, LocalDateTime joinedAt) {
        this.groupMemberId = groupMemberId;
        this.groupId = groupId;
        this.userId = userId;
        this.groupMemberType = groupMemberType;
        this.groupMemberStatus = groupMemberStatus;
        this.joinedAt = joinedAt;
    }

    public static GroupMember createHost(Long groupId, Long userId) {
        return GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .groupMemberType(GroupMemberType.HOST)
                .groupMemberStatus(GroupMemberStatus.JOINED)
                .joinedAt(LocalDateTime.now())
                .build();
    }


}


