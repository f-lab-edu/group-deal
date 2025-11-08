package com.app.groupdeal.domain.group;

import com.app.groupdeal.domain.common.BaseDomain;
import com.app.groupdeal.global.error.ErrorType;
import com.app.groupdeal.global.error.exception.BusinessException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Group extends BaseDomain {

    private Long groupId;
    private String productName;
    private String category;
    private String description;
    private String dividedUnit;
    private Integer originalPrice;
    private Integer targetParticipants;
    private Integer recruitmentMinutes;
    private LocalDateTime deadlineAt;
    private String meetingLocation;
    private LocalDateTime meetingAt;
    private Long hostMemberId;
    private GroupStatus status;
    private Integer currentParticipants;


    @Builder
    public Group(Long groupId, String productName, String category, String description, String dividedUnit,
                 Integer originalPrice, Integer targetParticipants, Integer recruitmentMinutes, LocalDateTime deadlineAt,
                 String meetingLocation, LocalDateTime meetingAt, Long hostMemberId, GroupStatus status, Integer currentParticipants) {
        this.groupId = groupId;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.dividedUnit = dividedUnit;
        this.originalPrice = originalPrice;
        this.targetParticipants = targetParticipants;
        this.recruitmentMinutes = recruitmentMinutes;
        this.deadlineAt = deadlineAt;
        this.meetingLocation = meetingLocation;
        this.meetingAt = meetingAt;
        this.hostMemberId = hostMemberId;
        this.status = status;
        this.currentParticipants = currentParticipants;
    }

    public static Group create(String productName, String category, String description, String dividedUnit, Integer originalPrice,
                               Integer targetParticipants, Integer recruitmentMinutes, String meetingLocation, LocalDateTime meetingAt, Long hostMemberId){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadlineAt = now.plusMinutes(recruitmentMinutes);

        if (meetingAt.isBefore(deadlineAt) || meetingAt.isEqual(deadlineAt)) {
            throw new BusinessException(ErrorType.INVALID_MEETING_DATETIME);
        }

        return Group.builder()
                .productName(productName)
                .category(category)
                .description(description)
                .dividedUnit(dividedUnit)
                .originalPrice(originalPrice)
                .targetParticipants(targetParticipants)
                .recruitmentMinutes(recruitmentMinutes)
                .deadlineAt(deadlineAt)
                .meetingLocation(meetingLocation)
                .meetingAt(meetingAt)
                .hostMemberId(hostMemberId)
                .status(GroupStatus.RECRUITING)
                .currentParticipants(1)
                .build();
    }

    public Integer calculatePricePerPerson() {
        return originalPrice / targetParticipants;
    }


}
