package com.app.groupdeal.application.group;

import com.app.groupdeal.application.group.service.GroupMemberService;
import com.app.groupdeal.application.group.service.GroupService;
import com.app.groupdeal.domain.group.constants.GroupMemberStatus;
import com.app.groupdeal.domain.group.constants.GroupMemberType;
import com.app.groupdeal.domain.group.constants.GroupStatus;
import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.model.GroupMember;
import com.app.groupdeal.domain.group.repository.GroupRepository;
import com.app.groupdeal.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("GroupService 통합테스트")
class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    @DisplayName("그룹 생성 성공")
    void createGroup_Success() {
        // given
        Group group = Group.create(
                "코스트코 견과류 3kg",
                "식품",
                "신선한 견과류입니다",
                "500g씩",
                30000,
                6,
                1440,
                "강남역 3번 출구",
                LocalDateTime.now().plusDays(2),
                1L,
                "홍길동"
        );

        // when
        Group savedGroup = groupService.createGroup(group);

        // then
        assertThat(savedGroup.getGroupId()).isNotNull();
        assertThat(savedGroup.getProductName()).isEqualTo("코스트코 견과류 3kg");
        assertThat(savedGroup.getStatus()).isEqualTo(GroupStatus.RECRUITING);
        assertThat(savedGroup.getCurrentParticipants()).isEqualTo(1);
    }

    @Test
    @DisplayName("그룹 생성 시 호스트가 자동으로 참여자에 추가됨")
    void createGroup_HostAutoJoined() {
        // given
        Group group = Group.create(
                "테스트 상품",
                "식품",
                "설명",
                "단위",
                10000,
                5,
                1440,
                "강남역",
                LocalDateTime.now().plusDays(2),
                1L,
                "테스트유저"
        );

        // when
        Group savedGroup = groupService.createGroup(group);

        // then
        assertThat(savedGroup.getCurrentParticipants()).isEqualTo(1);
        assertThat(savedGroup.getHostMemberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("그룹 생성 시 GroupMember에 호스트가 HOST 타입으로 추가됨")
    void createGroup_HostAddedAsGroupMember() {
        // given
        Group group = Group.create(
                "테스트 상품",
                "식품",
                "설명",
                "단위",
                10000,
                5,
                1440,
                "강남역",
                LocalDateTime.now().plusDays(2),
                1L,
                "테스트유저"
        );

        // when
        Group savedGroup = groupService.createGroup(group);

        // GroupMemberService를 통해 참여자 확인
        List<GroupMember> members = groupMemberService.findJoinedMembers(savedGroup.getGroupId());

        // then
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getUserId()).isEqualTo(1L);
        assertThat(members.get(0).getNickname()).isEqualTo("테스트유저");
        assertThat(members.get(0).getGroupMemberType()).isEqualTo(GroupMemberType.HOST);
        assertThat(members.get(0).getGroupMemberStatus()).isEqualTo(GroupMemberStatus.JOINED);
        assertThat(members.get(0).getJoinedAt()).isNotNull();
    }

    @Test
    @DisplayName("그룹 목록 조회 - 페이징")
    void searchGroup_FirstPage() {
        // given
        for (int i = 1; i <= 25; i++) {
            Group group = Group.create(
                    "상품" + i,
                    "식품",
                    "설명" + i,
                    "단위",
                    10000,
                    5,
                    1440,
                    "강남역",
                    LocalDateTime.now().plusDays(2),
                    1L,
                    "호스트"
            );
            groupService.createGroup(group);
        }

        // when
        Page<Group> page = groupService.searchGroup(0, 10);

        // then
        assertThat(page.getTotalElements()).isEqualTo(25);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
        assertThat(page.hasNext()).isTrue();
        assertThat(page.hasPrevious()).isFalse();
    }

    @Test
    @DisplayName("그룹 목록 조회 - 최신순 정렬")
    void searchGroup_OrderByCreatedTimeDesc() {
        // given
        Group group1 = Group.create(
                "첫 번째 상품",
                "식품",
                "설명1",
                "단위",
                10000,
                5,
                1440,
                "강남역",
                LocalDateTime.now().plusDays(2),
                1L,
                "호스트"
        );
        Group savedGroup1 = groupService.createGroup(group1);

        try {
            Thread.sleep(100); // 시간 차이를 위한 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Group group2 = Group.create(
                "두 번째 상품",
                "식품",
                "설명2",
                "단위",
                20000,
                5,
                1440,
                "신촌역",
                LocalDateTime.now().plusDays(2),
                1L,
                "호스트"
        );
        Group savedGroup2 = groupService.createGroup(group2);

        // when
        Page<Group> page = groupService.searchGroup(0, 10);

        // then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent().get(0).getProductName()).isEqualTo("두 번째 상품");
        assertThat(page.getContent().get(1).getProductName()).isEqualTo("첫 번째 상품");
    }

    @Test
    @DisplayName("그룹 단건 조회 성공")
    void getGroup_Success() {
        // given
        Group group = Group.create(
                "조회 테스트",
                "식품",
                "설명",
                "단위",
                20000,
                4,
                1440,
                "신촌역",
                LocalDateTime.now().plusDays(2),
                1L,
                "호스트"
        );
        Group savedGroup = groupService.createGroup(group);

        // when
        Group foundGroup = groupService.findById(savedGroup.getGroupId());

        // then
        assertThat(foundGroup).isNotNull();
        assertThat(foundGroup.getGroupId()).isEqualTo(savedGroup.getGroupId());
        assertThat(foundGroup.getProductName()).isEqualTo("조회 테스트");
    }

    @Test
    @DisplayName("존재하지 않는 그룹 조회 시 예외 발생")
    void getGroup_NotFound_Fail() {
        // when & then
        assertThatThrownBy(() -> groupService.findById(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("그룹");
    }

    @Test
    @DisplayName("그룹 상세 조회 - 모든 정보 확인")
    void getGroup_WithAllDetails() {
        // given
        Group group = Group.create(
                "코스트코 견과류 3kg",
                "식품",
                "신선한 견과류입니다.\n소분해서 나눠드려요.",
                "500g씩",
                30000,
                6,
                1440,
                "강남역 3번 출구",
                LocalDateTime.now().plusDays(2),
                1L,
                "호스트유저"
        );
        Group savedGroup = groupService.createGroup(group);

        // when
        Group foundGroup = groupService.findById(savedGroup.getGroupId());

        // then
        // 기본 정보
        assertThat(foundGroup.getGroupId()).isEqualTo(savedGroup.getGroupId());
        assertThat(foundGroup.getProductName()).isEqualTo("코스트코 견과류 3kg");
        assertThat(foundGroup.getCategory()).isEqualTo("식품");
        assertThat(foundGroup.getDescription()).isEqualTo("신선한 견과류입니다.\n소분해서 나눠드려요.");

        // 가격 정보
        assertThat(foundGroup.getOriginalPrice()).isEqualTo(30000);
        assertThat(foundGroup.calculatePricePerPersonRoundedUp()).isEqualTo(5000);
        assertThat(foundGroup.calculateTotalAmountByRoundedUp()).isEqualTo(30000);
        assertThat(foundGroup.calculateDifferenceAmount()).isEqualTo(0);

        // 참여 정보
        assertThat(foundGroup.getTargetParticipants()).isEqualTo(6);
        assertThat(foundGroup.getCurrentParticipants()).isEqualTo(1);
        assertThat(foundGroup.calculateProgressRate()).isEqualTo(17);

        // 상태 정보
        assertThat(foundGroup.getStatus()).isEqualTo(GroupStatus.RECRUITING);
        assertThat(foundGroup.getRecruitmentMinutes()).isEqualTo(1440);
        assertThat(foundGroup.getDeadlineAt()).isNotNull();
        assertThat(foundGroup.calculateRemainingMinutes()).isGreaterThan(0);

        // 거래 정보
        assertThat(foundGroup.getMeetingLocation()).isEqualTo("강남역 3번 출구");
        assertThat(foundGroup.getMeetingAt()).isNotNull();
        assertThat(foundGroup.getDividedUnit()).isEqualTo("500g씩");

        // 호스트 정보
        assertThat(foundGroup.getHostMemberId()).isEqualTo(1L);
        assertThat(foundGroup.getHostMemberName()).isEqualTo("호스트유저");

        // 생성 시간
        assertThat(foundGroup.getCreatedTime()).isNotNull();
    }

}
