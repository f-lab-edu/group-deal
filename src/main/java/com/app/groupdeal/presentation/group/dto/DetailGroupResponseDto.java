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
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailGroupResponseDto {

    private Long groupId;
    private String productName;
    private String category;
    private String description;
    private Integer originalPrice;
    private Integer pricePerPerson;
    private Integer totalAmount;
    private Integer differenceAmount;
    private Integer targetParticipants;
    private Integer currentParticipants;
    private String dividedUnit;
    private Integer recruitmentMinutes;
    private GroupStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadlineAt;
    private String meetingLocation;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime meetingAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer progressRate;
    private Integer remainingMinutes;

    private HostInfo host;
    private List<MemberInfo> members;


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HostInfo {
        private Long userId;
        private String nickname;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberInfo {
        private Long userId;
        private String nickname;
        private GroupMemberType memberType;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime joinedAt;
    }

    public static DetailGroupResponseDto of(Group group, List<GroupMember> groupMembers) {
        return DetailGroupResponseDto.builder()
                .groupId(group.getGroupId())
                .productName(group.getProductName())
                .category(group.getCategory())
                .description(group.getDescription())
                .originalPrice(group.getOriginalPrice())
                .pricePerPerson(group.calculatePricePerPersonRoundedUp())
                .totalAmount(group.calculateTotalAmountByRoundedUp())
                .differenceAmount(group.calculateDifferenceAmount())
                .targetParticipants(group.getTargetParticipants())
                .currentParticipants(group.getCurrentParticipants())
                .dividedUnit(group.getDividedUnit())
                .recruitmentMinutes(group.getRecruitmentMinutes())
                .status(group.getStatus())
                .deadlineAt(group.getDeadlineAt())
                .meetingLocation(group.getMeetingLocation())
                .meetingAt(group.getMeetingAt())
                .createdAt(group.getCreatedTime())
                .progressRate(group.calculateProgressRate())
                .remainingMinutes(group.calculateRemainingMinutes())
                .host(HostInfo.builder()
                        .userId(group.getHostMemberId())
                        .nickname(group.getHostMemberName())
                        .build())
                .members(groupMembers.stream()
                        .map(member -> MemberInfo.builder()
                                .userId(member.getUserId())
                                .nickname(member.getNickname())
                                .memberType(member.getGroupMemberType())
                                .joinedAt(member.getJoinedAt())
                                .build())
                        .toList())
                .build();
    }

}
