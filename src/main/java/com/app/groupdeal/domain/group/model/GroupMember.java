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
    private String nickname;
    private GroupMemberType groupMemberType;
    private GroupMemberStatus groupMemberStatus;
    private LocalDateTime joinedAt;

    @Builder
    public GroupMember(Long groupMemberId, Long groupId, Long userId, String nickname, GroupMemberType groupMemberType,
                       GroupMemberStatus groupMemberStatus, LocalDateTime joinedAt) {
        this.groupMemberId = groupMemberId;
        this.groupId = groupId;
        this.userId = userId;
        this.nickname = nickname;
        this.groupMemberType = groupMemberType;
        this.groupMemberStatus = groupMemberStatus;
        this.joinedAt = joinedAt;
    }

    public static GroupMember createHost(Group group) {
        return GroupMember.builder()
                .groupId(group.getGroupId())
                .userId(group.getHostMemberId())
                .nickname(group.getHostMemberName())
                .groupMemberType(GroupMemberType.HOST)
                .groupMemberStatus(GroupMemberStatus.JOINED)
                .joinedAt(LocalDateTime.now())
                .build();
    }


    public static GroupMember createMember(Long groupId, Long userId, String nickname) {
        return GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .nickname(nickname)
                .groupMemberType(GroupMemberType.MEMBER)
                .groupMemberStatus(GroupMemberStatus.JOINED)
                .joinedAt(LocalDateTime.now())
                .build();
    }
}


