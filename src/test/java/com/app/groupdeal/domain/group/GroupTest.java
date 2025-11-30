package com.app.groupdeal.domain.group;

import com.app.groupdeal.domain.group.constants.GroupMemberStatus;
import com.app.groupdeal.domain.group.constants.GroupMemberType;
import com.app.groupdeal.domain.group.constants.GroupStatus;
import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.model.GroupMember;
import com.app.groupdeal.global.error.ErrorType;
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

    @Test
    @DisplayName("참여 가능 여부 검증 - 모집중이 아닌 경우 실패")
    void validateJoinable_NotRecruiting_Fail() {
        // given - Builder로 직접 CLOSED 상태로 생성
        Group group = Group.builder()
                .productName("코스트코 견과류")
                .category("식품")
                .description("신선한 견과류")
                .dividedUnit("500g씩")
                .originalPrice(30000)
                .targetParticipants(6)
                .currentParticipants(6)
                .status(GroupStatus.CLOSED) 
                .recruitmentMinutes(1440)
                .deadlineAt(LocalDateTime.now().plusDays(1))
                .meetingLocation("강남역 3번 출구")
                .meetingAt(LocalDateTime.now().plusDays(2))
                .hostMemberId(1L)
                .hostMemberName("홍길동")
                .build();

        // when & then
        assertThatThrownBy(() -> group.validateJoinable())
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.GROUP_NOT_RECRUITING);
    }

    @Test
    @DisplayName("참여 가능 여부 검증 - 인원 초과 시 실패")
    void validateJoinable_Full_Fail() {
        // given
        Group group = Group.create(
                "테스트", "식품", "설명", "단위",
                10000, 2, 1440, "강남역",
                LocalDateTime.now().plusDays(2),
                1L, "호스트"
        );
        group.increaseParticipant();

        // when & then
        assertThatThrownBy(group::validateJoinable)
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("초과");
    }

    @Test
    @DisplayName("참여자 증가 성공")
    void increaseParticipant_Success() {
        // given
        Group group = Group.create(
                "테스트", "식품", "설명", "단위",
                10000, 5, 1440, "강남역",
                LocalDateTime.now().plusDays(2),
                1L, "호스트"
        );
        int before = group.getCurrentParticipants();

        // when
        group.increaseParticipant();

        // then
        assertThat(group.getCurrentParticipants()).isEqualTo(before + 1);
    }

    @Test
    @DisplayName("참여자 증가 - 인원 초과 시 실패")
    void increaseParticipant_Full_Fail() {
        // given
        Group group = Group.create(
                "테스트", "식품", "설명", "단위",
                10000, 2, 1440, "강남역",
                LocalDateTime.now().plusDays(2),
                1L, "호스트"
        );
        group.increaseParticipant();

        // when & then
        assertThatThrownBy(group::increaseParticipant)
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("마감 시간이 지난 그룹은 참여 불가")
    void validateJoinable_DeadlinePassed() {
        // given
        Group group = Group.builder()
                .status(GroupStatus.RECRUITING)
                .currentParticipants(3)
                .targetParticipants(6)
                .deadlineAt(LocalDateTime.now().minusMinutes(1))
                .build();

        // when & then
        assertThatThrownBy(group::validateJoinable)
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.GROUP_DEADLINE_PASSED);
    }

    @Test
    @DisplayName("마감된 그룹은 나갈 수 없음")
    void validateLeavable_ClosedGroup() {
        // given
        Group group = Group.builder()
                .status(GroupStatus.CLOSED)
                .build();

        // when & then
        assertThatThrownBy(() -> group.validateLeavable())
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.CANNOT_LEAVE_CLOSED_GROUP);
    }

    @Test
    @DisplayName("참여자 수 감소 성공")
    void decreaseParticipant_Success() {
        // given
        Group group = Group.builder()
                .currentParticipants(3)
                .targetParticipants(6)
                .build();

        // when
        group.decreaseParticipant();

        // then
        assertThat(group.getCurrentParticipants()).isEqualTo(2);
    }

    @Test
    @DisplayName("참여자가 1명일 때 감소 불가")
    void decreaseParticipant_MinimumParticipant() {
        // given
        Group group = Group.builder()
                .currentParticipants(1)
                .targetParticipants(6)
                .build();

        // when & then
        assertThatThrownBy(group::decreaseParticipant)
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.CANNOT_DECREASE_PARTICIPANT);
    }

    @Test
    @DisplayName("그룹 나가기 성공")
    void leaveGroup_Success() {
        // given
        GroupMember member = GroupMember.createMember(1L, 2L, "김철수");

        // when
        member.leaveGroup();

        // then
        assertThat(member.getGroupMemberStatus()).isEqualTo(GroupMemberStatus.LEFT);
        assertThat(member.getLeftAt()).isNotNull();
    }

    @Test
    @DisplayName("호스트는 그룹을 나갈 수 없음")
    void leaveGroup_HostCannotLeave() {
        // given
        Group group = Group.builder()
                .groupId(1L)
                .hostMemberId(1L)
                .hostMemberName("홍길동")
                .build();
        GroupMember hostMember = GroupMember.createHost(group);

        // when & then
        assertThatThrownBy(() -> hostMember.leaveGroup())
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.HOST_CANNOT_LEAVE);
    }

    @Test
    @DisplayName("이미 나간 그룹은 다시 나갈 수 없음")
    void leaveGroup_AlreadyLeft() {
        // given
        GroupMember member = GroupMember.createMember(1L, 2L, "김철수");
        member.leaveGroup();

        // when & then
        assertThatThrownBy(() -> member.leaveGroup())
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ALREADY_LEFT);
    }

    @Test
    @DisplayName("그룹 재참여 성공")
    void joinGroup_Success() {
        // given
        GroupMember member = GroupMember.builder()
                .groupId(1L)
                .userId(2L)
                .nickname("김철수")
                .groupMemberType(GroupMemberType.MEMBER)
                .groupMemberStatus(GroupMemberStatus.LEFT)
                .leftAt(LocalDateTime.now().minusDays(1))
                .build();

        // when
        member.joinGroup();

        // then
        assertThat(member.getGroupMemberStatus()).isEqualTo(GroupMemberStatus.JOINED);
        assertThat(member.getJoinedAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 참여한 그룹에 재참여 불가")
    void joinGroup_AlreadyJoined() {
        // given
        GroupMember member = GroupMember.createMember(1L, 2L, "김철수");

        // when & then
        assertThatThrownBy(() -> member.joinGroup())
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ALREADY_JOINED);
    }
}
