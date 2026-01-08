package com.app.groupdeal.presentation.group.dto;

import com.app.groupdeal.domain.group.constants.GroupMemberType;
import com.app.groupdeal.domain.group.constants.GroupStatus;
import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.model.GroupMember;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinGroupResponseDto {

    private Long groupId;
    private String productName;
    private GroupStatus status;
    private Integer targetParticipants;
    private Integer currentParticipants;
    private Integer progressRate;

    private ProcessingStatus processingStatus;
    private String message;

    private MemberInfo joinMember;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberInfo {
        private Long userId;
        private String nickname;
        private GroupMemberType memberType;
        private Integer queueNumber;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime joinedAt;
    }

    public enum ProcessingStatus {
        PENDING,
        COMPLETED
    }

    public static JoinGroupResponseDto ofWithQueue(Group group, GroupMember groupMember) {
        return JoinGroupResponseDto.builder()
                .groupId(group.getGroupId())
                .productName(group.getProductName())
                .status(group.getStatus())
                .targetParticipants(group.getTargetParticipants())
                .currentParticipants(group.getCurrentParticipants())
                .progressRate(group.calculateProgressRate())
                .processingStatus(ProcessingStatus.COMPLETED)
                .joinMember(MemberInfo.builder()
                        .userId(groupMember.getUserId())
                        .nickname(groupMember.getNickname())
                        .memberType(groupMember.getGroupMemberType())
                        .queueNumber(groupMember.getQueueNumber())
                        .joinedAt(groupMember.getJoinedAt())
                        .build())
                .build();
    }

    public static JoinGroupResponseDto pending(
            Group group,
            Long userId,
            String nickname,
            Long queueNumber) {
        return JoinGroupResponseDto.builder()
                .groupId(group.getGroupId())
                .productName(group.getProductName())
                .status(group.getStatus())
                .targetParticipants(group.getTargetParticipants())
                .currentParticipants(group.getCurrentParticipants())
                .progressRate(group.calculateProgressRate())
                .processingStatus(ProcessingStatus.PENDING)
                .message("참여 요청이 접수되었습니다")
                .joinMember(MemberInfo.builder()
                        .userId(userId)
                        .nickname(nickname)
                        .memberType(null)
                        .queueNumber(queueNumber.intValue())
                        .joinedAt(null)
                        .build())
                .build();
    }

    public static JoinGroupResponseDto of(Group group, GroupMember groupMember) {
        return JoinGroupResponseDto.builder()
                .groupId(group.getGroupId())
                .productName(group.getProductName())
                .status(group.getStatus())
                .targetParticipants(group.getTargetParticipants())
                .currentParticipants(group.getCurrentParticipants())
                .progressRate(group.calculateProgressRate())
                .processingStatus(ProcessingStatus.COMPLETED)
                .joinMember(MemberInfo.builder()
                        .userId(groupMember.getUserId())
                        .nickname(groupMember.getNickname())
                        .memberType(groupMember.getGroupMemberType())
                        .joinedAt(groupMember.getJoinedAt())
                        .build())
                .build();
    }

}
