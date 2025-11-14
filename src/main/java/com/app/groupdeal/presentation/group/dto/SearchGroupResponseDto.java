package com.app.groupdeal.presentation.group.dto;

import com.app.groupdeal.domain.group.constants.GroupStatus;
import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchGroupResponseDto {

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
    private Long hostMemberId;
    private String hostMemberName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer progressRate;
    private Integer remainingMinutes;

    public static SearchGroupResponseDto of(Group group) {
        return SearchGroupResponseDto.builder()
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
                .hostMemberId(group.getHostMemberId())
                .hostMemberName(group.getHostMemberName())
                .createdAt(LocalDateTime.now())
                .build();
    }


}
