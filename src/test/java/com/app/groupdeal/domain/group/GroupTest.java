package com.app.groupdeal.domain.group;

import com.app.groupdeal.domain.group.constants.GroupStatus;
import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("Group Domain 단위테스트")
class GroupTest {

    @Test
    @DisplayName("그룹 생성 성공")
    void createGroup_Success() {
        // given
        String productName = "코스트코 견과류";
        String category = "식품";
        String description = "신선한 견과류";
        String dividedUnit = "500g씩";
        Integer originalPrice = 30000;
        Integer targetParticipants = 6;
        Integer recruitmentMinutes = 1440;
        String meetingLocation = "강남역 3번 출구";
        Long hostMemberId = 1L;
        String hostMemberName = "홍길동";
        LocalDateTime meetingAt = LocalDateTime.now().plusDays(2);

        // when
        Group group = Group.create(
                productName, category, description, dividedUnit,
                originalPrice, targetParticipants, recruitmentMinutes,
                meetingLocation, meetingAt,
                hostMemberId, hostMemberName
        );

        // then
        assertThat(group).isNotNull();
        assertThat(group.getProductName()).isEqualTo(productName);
        assertThat(group.getStatus()).isEqualTo(GroupStatus.RECRUITING);
        assertThat(group.getCurrentParticipants()).isEqualTo(1);
        assertThat(group.getHostMemberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("거래 시간이 마감 시간보다 이전이면 예외 발생")
    void createGroup_InvalidMeetingTime_Fail() {
        // given
        LocalDateTime meetingAt = LocalDateTime.now().plusMinutes(30); // 30분 후
        int recruitmentMinutes = 1440; // 24시간 모집

        // when & then
        assertThatThrownBy(() -> Group.create(
                "테스트 상품", "식품", "설명", "단위", 10000,
                5, recruitmentMinutes, "강남역", meetingAt, 1L,
                "홍길동"
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("거래 일시");
    }

    @Test
    @DisplayName("1인당 가격 계산 - 올림 처리")
    void calculatePricePerPerson() {
        // given
        Group group = Group.builder()
                .originalPrice(10000)
                .targetParticipants(3)
                .build();

        // when
        Integer pricePerPerson = group.calculatePricePerPersonRoundedUp();

        // then
        assertThat(pricePerPerson).isEqualTo(3334);
    }

    @Test
    @DisplayName("총 금액 계산")
    void calculateTotalAmount() {
        // given
        Group group = Group.builder()
                .originalPrice(10000)
                .targetParticipants(3)
                .build();

        // when
        Integer totalAmount = group.calculateTotalAmountByRoundedUp();

        // then
        assertThat(totalAmount).isEqualTo(10002);
    }

    @Test
    @DisplayName("차액 계산")
    void calculateDifferenceAmount() {
        // given
        Group group = Group.builder()
                .originalPrice(10000)
                .targetParticipants(3)
                .build();

        // when
        Integer difference = group.calculateDifferenceAmount();

        // then
        assertThat(difference).isEqualTo(2);
    }

    @Test
    @DisplayName("참여율 계산 - 반올림")
    void calculateProgressRate_33Percent() {
        // given
        Group group = Group.builder()
                .currentParticipants(2)
                .targetParticipants(6)
                .build();

        // when
        Integer progressRate = group.calculateProgressRate();

        // then
        assertThat(progressRate).isEqualTo(33);
    }

    @Test
    @DisplayName("남은 시간 계산 - 마감 전")
    void calculateRemainingMinutes_BeforeDeadline() {
        // given
        LocalDateTime deadline = LocalDateTime.now().plusHours(2);
        Group group = Group.builder()
                .deadlineAt(deadline)
                .build();

        // when
        Integer remainingMinutes = group.calculateRemainingMinutes();

        // then
        assertThat(remainingMinutes).isGreaterThanOrEqualTo(119);
        assertThat(remainingMinutes).isLessThanOrEqualTo(120);
    }

}
