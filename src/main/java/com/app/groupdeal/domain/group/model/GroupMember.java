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

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String createdBy;
    private String updatedBy;

    @Builder
    public GroupMember(Long groupMemberId, Long groupId, Long userId, GroupMemberType groupMemberType, GroupMemberStatus groupMemberStatus,
                       LocalDateTime joinedAt, LocalDateTime createdTime, LocalDateTime updatedTime, String createdBy, String updatedBy) {
        this.groupMemberId = groupMemberId;
        this.groupId = groupId;
        this.userId = userId;
        this.groupMemberType = groupMemberType;
        this.groupMemberStatus = groupMemberStatus;
        this.joinedAt = joinedAt;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
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


