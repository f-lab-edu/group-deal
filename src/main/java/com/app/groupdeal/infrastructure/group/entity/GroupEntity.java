package com.app.groupdeal.infrastructure.group.entity;

import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.constants.GroupStatus;
import com.app.groupdeal.infrastructure.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "`groups`")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(nullable = false, length = 100)
    private String productName;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer originalPrice;

    @Column(nullable = false)
    private Integer targetParticipants;

    @Column(length = 100)
    private String dividedUnit;

    @Column(nullable = false)
    private Integer recruitmentMinutes;

    @Column(nullable = false)
    private LocalDateTime deadlineAt;

    @Column(nullable = false)
    private String meetingLocation;

    @Column(nullable = false)
    private LocalDateTime meetingAt;

    @Column(nullable = false)
    private Long hostMemberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupStatus status;

    @Column(nullable = false)
    private Integer currentParticipants;

    @Builder(access = AccessLevel.PRIVATE)
    public GroupEntity(Long groupId, String productName, String category, String description, Integer originalPrice,
                       Integer targetParticipants, String dividedUnit, Integer recruitmentMinutes, LocalDateTime deadlineAt,
                       String meetingLocation, LocalDateTime meetingAt, Long hostMemberId, GroupStatus status, Integer currentParticipants) {
        this.groupId = groupId;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.originalPrice = originalPrice;
        this.targetParticipants = targetParticipants;
        this.dividedUnit = dividedUnit;
        this.recruitmentMinutes = recruitmentMinutes;
        this.deadlineAt = deadlineAt;
        this.meetingLocation = meetingLocation;
        this.meetingAt = meetingAt;
        this.hostMemberId = hostMemberId;
        this.status = status;
        this.currentParticipants = currentParticipants;
    }

    public static GroupEntity from(Group group){
        return GroupEntity.builder()
                .productName(group.getProductName())
                .category(group.getCategory())
                .description(group.getDescription())
                .originalPrice(group.getOriginalPrice())
                .targetParticipants(group.getTargetParticipants())
                .dividedUnit(group.getDividedUnit())
                .recruitmentMinutes(group.getRecruitmentMinutes())
                .deadlineAt(group.getDeadlineAt())
                .meetingLocation(group.getMeetingLocation())
                .meetingAt(group.getMeetingAt())
                .hostMemberId(group.getHostMemberId())
                .status(group.getStatus())
                .currentParticipants(group.getCurrentParticipants())
                .build();
    }

    public Group toDomain(){
        return Group.builder()
                .groupId(groupId)
                .productName(productName)
                .category(category)
                .description(description)
                .originalPrice(originalPrice)
                .targetParticipants(targetParticipants)
                .dividedUnit(dividedUnit)
                .recruitmentMinutes(recruitmentMinutes)
                .deadlineAt(deadlineAt)
                .meetingLocation(meetingLocation)
                .meetingAt(meetingAt)
                .hostMemberId(hostMemberId)
                .status(status)
                .currentParticipants(currentParticipants)
                .createdTime(this.getCreatedTime())
                .build();
    }
}
