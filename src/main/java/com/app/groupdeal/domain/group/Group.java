package com.app.groupdeal.domain.group;

import com.app.groupdeal.domain.common.BaseDomain;
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
    private LocalDateTime deadlineAt;
    private String meetingLocation;
    private LocalDateTime meetingAt;
    private Long hostUserId;
    private GroupStatus status;
    private Integer currentCount;


    @Builder
    public Group(Long groupId, String productName, String category, String description, String dividedUnit,
                 Integer originalPrice, Integer targetParticipants, LocalDateTime deadlineAt, String meetingLocation,
                 LocalDateTime meetingAt, Long hostUserId, GroupStatus status, Integer currentCount) {
        this.groupId = groupId;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.dividedUnit = dividedUnit;
        this.originalPrice = originalPrice;
        this.targetParticipants = targetParticipants;
        this.deadlineAt = deadlineAt;
        this.meetingLocation = meetingLocation;
        this.meetingAt = meetingAt;
        this.hostUserId = hostUserId;
        this.status = status;
        this.currentCount = currentCount;
    }


}
