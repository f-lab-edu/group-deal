package com.app.groupdeal.domain.group.model;

import com.app.groupdeal.domain.common.BaseDomain;
import com.app.groupdeal.domain.group.constants.GroupStatus;
import com.app.groupdeal.global.error.ErrorType;
import com.app.groupdeal.global.error.exception.BusinessException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    private String hostMemberName;
    private GroupStatus status;
    private Integer currentParticipants;


    @Builder
    public Group(Long groupId, String productName, String category, String description, String dividedUnit,
                 Integer originalPrice, Integer targetParticipants, Integer recruitmentMinutes, LocalDateTime deadlineAt,
                 String meetingLocation, LocalDateTime meetingAt, Long hostMemberId, String hostMemberName, GroupStatus status, Integer currentParticipants,
                 LocalDateTime createdTime) {
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
        this.hostMemberName = hostMemberName;
        this.status = status;
        this.currentParticipants = currentParticipants;
        this.createdTime = createdTime;
    }

    public static Group create(String productName, String category, String description, String dividedUnit, Integer originalPrice,
                               Integer targetParticipants, Integer recruitmentMinutes, String meetingLocation, LocalDateTime meetingAt,
                               Long hostMemberId, String hostMemberName){

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
                .hostMemberName(hostMemberName)
                .status(GroupStatus.RECRUITING)
                .currentParticipants(1)
                .build();
    }

    public Integer calculatePricePerPersonRoundedUp() {
        return (int) Math.ceil((double) originalPrice / targetParticipants);
    }

    public Integer calculateTotalAmountByRoundedUp() {
        return calculatePricePerPersonRoundedUp() * targetParticipants;
    }

    public Integer calculateDifferenceAmount() {
        return calculateTotalAmountByRoundedUp() - originalPrice;
    }

    public Integer calculateProgressRate() {
        double rate = ((double) currentParticipants / targetParticipants) * 100;
        return (int) Math.round(rate);
    }

    public Integer calculateRemainingMinutes(){

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(deadlineAt)) {
            return 0;
        }

        long remainingMinutes = ChronoUnit.MINUTES.between(now, deadlineAt);

        return (int) remainingMinutes;
    }


    public void validateJoinable() {
        if (this.status != GroupStatus.RECRUITING) {
            throw new BusinessException(ErrorType.GROUP_NOT_RECRUITING);
        }

        if (this.currentParticipants >= this.targetParticipants) {
            throw new BusinessException(ErrorType.GROUP_FULL);
        }

        if (this.deadlineAt.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorType.GROUP_DEADLINE_PASSED);
        }
    }

    public void increaseParticipant() {
        if (this.currentParticipants >= this.targetParticipants) {
            throw new BusinessException(ErrorType.GROUP_FULL);
        }
        this.currentParticipants++;
    }


}
