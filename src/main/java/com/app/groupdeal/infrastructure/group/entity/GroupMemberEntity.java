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
@Table(
        name = "group_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_group_user_status",
                        columnNames = {"group_id", "user_id", "group_member_status"}
                )
        },
        indexes = {
                @Index(name = "idx_group_queue", columnList = "group_id, queue_number")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupMemberId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupMemberType groupMemberType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupMemberStatus groupMemberStatus;

    @Column(name = "queue_number")
    private Integer queueNumber;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    @Builder(access = AccessLevel.PRIVATE)
    public GroupMemberEntity(Long groupMemberId, Long groupId, Long userId, String nickname, GroupMemberType groupMemberType,
                             GroupMemberStatus groupMemberStatus, Integer queueNumber, LocalDateTime joinedAt, LocalDateTime leftAt) {
        this.groupMemberId = groupMemberId;
        this.groupId = groupId;
        this.userId = userId;
        this.nickname = nickname;
        this.groupMemberType = groupMemberType;
        this.groupMemberStatus = groupMemberStatus;
        this.queueNumber = queueNumber;
        this.joinedAt = joinedAt;
        this.leftAt = leftAt;
    }

    public static GroupMemberEntity from(GroupMember groupMember) {
        return GroupMemberEntity.builder()
                .groupMemberId(groupMember.getGroupMemberId())
                .groupId(groupMember.getGroupId())
                .userId(groupMember.getUserId())
                .nickname(groupMember.getNickname())
                .groupMemberType(groupMember.getGroupMemberType())
                .groupMemberStatus(groupMember.getGroupMemberStatus())
                .queueNumber(groupMember.getQueueNumber())
                .joinedAt(groupMember.getJoinedAt())
                .leftAt(groupMember.getLeftAt())
                .build();
    }

    public GroupMember toDomain(){
        return GroupMember.builder()
                .groupMemberId(groupMemberId)
                .groupId(groupId)
                .userId(userId)
                .nickname(nickname)
                .groupMemberType(groupMemberType)
                .groupMemberStatus(groupMemberStatus)
                .queueNumber(queueNumber)
                .joinedAt(joinedAt)
                .leftAt(leftAt)
                .build();
    }
}
