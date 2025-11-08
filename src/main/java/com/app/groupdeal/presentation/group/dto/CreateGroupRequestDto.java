package com.app.groupdeal.presentation.group.dto;

import com.app.groupdeal.domain.group.Group;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequestDto {

    @NotBlank(message = "상품명은 필수입니다")
    @Size(max = 100, message = "상품명은 100자 이내여야 합니다")
    private String productName;

    @NotBlank(message = "카테고리는 필수입니다")
    private String category;

    @Size(max = 500, message = "상품 설명은 500자 이내여야 합니다")
    private String description;

    private String dividedUnit; // 소분 단위 (선택)

    @NotNull(message = "원가는 필수입니다")
    @Min(value = 0, message = "원가는 0 이상이어야 합니다")
    private Integer originalPrice;

    @NotNull(message = "목표 인원은 필수입니다")
    @Min(value = 2, message = "목표 인원은 최소 2명이어야 합니다")
    @Max(value = 1440, message = "모집 시간은 최대 24시간이어야 합니다")
    private Integer targetParticipants;

    @NotNull(message = "모집 시간은 필수입니다")
    @Min(value = 30, message = "모집 시간은 최소 30분이어야 합니다")
    @Max(value = 1440, message = "모집 시간은 최대 24시간(1440분)이어야 합니다")
    private Integer recruitmentMinutes; // 30, 60, 180, 360, 720, 1440

    @NotBlank(message = "거래 장소는 필수입니다")
    @Size(max = 200, message = "거래 장소는 200자 이내여야 합니다")
    private String meetingLocation;

    @NotNull(message = "거래 일시는 필수입니다")
    @Future(message = "거래 일시는 미래 시각이어야 합니다")
    private LocalDateTime meetingAt;

    public Group toDomain(Long hostMemberId){
        return Group.create(
                this.productName,
                this.category,
                this.description,
                this.dividedUnit,
                this.originalPrice,
                this.targetParticipants,
                this.recruitmentMinutes,
                this.meetingLocation,
                this.meetingAt,
                hostMemberId
        );
    }

}



