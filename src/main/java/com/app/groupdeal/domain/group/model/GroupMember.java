package com.app.groupdeal.domain.group.model;

import com.app.groupdeal.domain.common.BaseDomain;
import com.app.groupdeal.domain.group.constants.GroupMemberStatus;
import com.app.groupdeal.domain.group.constants.GroupMemberType;
import com.app.groupdeal.global.error.ErrorType;
import com.app.groupdeal.global.error.exception.BusinessException;
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
    private Integer queueNumber;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;


    @Builder
    public GroupMember(Long groupMemberId, Long groupId, Long userId, String nickname, GroupMemberType groupMemberType,
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

    public static GroupMember createHost(Group group) {
        return GroupMember.builder()
                .groupId(group.getGroupId())
                .userId(group.getHostMemberId())
                .nickname(group.getHostMemberName())
                .groupMemberType(GroupMemberType.HOST)
                .groupMemberStatus(GroupMemberStatus.JOINED)
                .queueNumber(1)
                .joinedAt(LocalDateTime.now())
                .leftAt(null)
                .build();
    }

    public static GroupMember createMemberWithQueue(Long groupId, Long userId, String nickname,
                                                    Integer queueNumber, GroupMemberStatus status) {
        return GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .nickname(nickname)
                .groupMemberType(GroupMemberType.MEMBER)
                .groupMemberStatus(status)
                .queueNumber(queueNumber)
                .joinedAt(LocalDateTime.now())
                .leftAt(null)
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
                .leftAt(null)
                .build();
    }

    public boolean isHost(){
        return this.groupMemberType == GroupMemberType.HOST;
    }

    public boolean isLeft(){
        return this.groupMemberStatus == GroupMemberStatus.LEFT;
    }

    public void leaveGroup() {
        if(this.groupMemberType == GroupMemberType.HOST){
            throw new BusinessException(ErrorType.HOST_CANNOT_LEAVE);
        }
        if(this.groupMemberStatus == GroupMemberStatus.LEFT){
            throw new BusinessException(ErrorType.ALREADY_LEFT);
        }
        this.groupMemberStatus = GroupMemberStatus.LEFT;
        this.leftAt = LocalDateTime.now();
    }

    public void joinGroup() {

        if(this.groupMemberStatus == GroupMemberStatus.JOINED){
            throw new BusinessException(ErrorType.ALREADY_JOINED);
        }

        this.groupMemberStatus = GroupMemberStatus.JOINED;
        this.joinedAt = LocalDateTime.now();
    }
}


