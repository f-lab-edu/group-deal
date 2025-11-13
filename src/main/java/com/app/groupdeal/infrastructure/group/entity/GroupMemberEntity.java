package com.app.groupdeal.infrastructure.group.entity;

import com.app.groupdeal.domain.group.model.GroupMember;
import com.app.groupdeal.domain.group.constants.GroupMemberStatus;
import com.app.groupdeal.domain.group.constants.GroupMemberType;
import com.app.groupdeal.infrastructure.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "group_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupMemberId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupMemberType groupMemberType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupMemberStatus groupMemberStatus;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Builder(access = AccessLevel.PRIVATE)
    public GroupMemberEntity(Long groupId, Long userId, GroupMemberType groupMemberType,
                             GroupMemberStatus groupMemberStatus, LocalDateTime joinedAt) {
        this.groupId = groupId;
        this.userId = userId;
        this.groupMemberType = groupMemberType;
        this.groupMemberStatus = groupMemberStatus;
        this.joinedAt = joinedAt;
    }

    public static GroupMemberEntity from(GroupMember groupMember) {
        return GroupMemberEntity.builder()
                .groupId(groupMember.getGroupId())
                .userId(groupMember.getUserId())
                .groupMemberType(groupMember.getGroupMemberType())
                .groupMemberStatus(groupMember.getGroupMemberStatus())
                .joinedAt(groupMember.getJoinedAt())
                .build();
    }

    public GroupMember toDomain(){
        return GroupMember.builder()
                .groupMemberId(groupMemberId)
                .groupId(groupId)
                .userId(userId)
                .groupMemberType(groupMemberType)
                .groupMemberStatus(groupMemberStatus)
                .joinedAt(joinedAt)
                .build();
    }
}
