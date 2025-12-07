package com.app.groupdeal.presentation.group;

import com.app.groupdeal.application.group.facade.GroupFacadeService;
import com.app.groupdeal.application.group.service.GroupService;
import com.app.groupdeal.application.user.service.UserService;
import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.repository.GroupMemberRepository;
import com.app.groupdeal.domain.group.repository.GroupRepository;
import com.app.groupdeal.domain.user.User;
import com.app.groupdeal.domain.user.UserRepository;
import com.app.groupdeal.global.error.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("GroupController 동시성 테스트")
public class GroupConcurrencyTest {

    @Autowired
    private GroupFacadeService groupFacadeService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    private Long testGroupId;
    private Long hostUserId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        groupRepository.deleteAll();
        groupMemberRepository.deleteAll();

        User hostUser = User.builder()
                .email("host@test.com")
                .password("1234")
                .nickname("호스트")
                .build();
        User savedHostUser = userService.createUser(hostUser);
        hostUserId = savedHostUser.getUserId();

        Group testGroup = Group.create(
                "코스트코 견과류 3kg",
                "식품",
                "신선한 견과류입니다",
                "500g씩",
                30000,
                3,
                1440,
                "강남역 3번 출구",
                LocalDateTime.now().plusDays(2),
                hostUserId,
                "호스트"
        );
        Group savedGroup = groupService.createGroup(testGroup);
        testGroupId = savedGroup.getGroupId();
    }

    @Test
    @DisplayName("비관적 락 적용 후 - 동시 참여 테스트")
    void concurrentJoin_WithPessimisticLock() throws InterruptedException {

        // given
        User firstUser = User.builder()
                .email("first@test.com")
                .password("1234")
                .nickname("첫번째참여자")
                .build();
        User savedFirstUser = userService.createUser(firstUser);
        groupFacadeService.joinGroup(testGroupId, savedFirstUser.getUserId(), savedFirstUser.getNickname());

        int numberOfUsers = 10;
        List<User> users = new ArrayList<>();

        for (int i = 0; i < numberOfUsers; i++) {
            User user = User.builder()
                    .email("user" + i + "@test.com")
                    .password("1234")
                    .nickname("사용자" + i)
                    .build();
            users.add(userService.createUser(user));
        }

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch latch = new CountDownLatch(numberOfUsers);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (User user : users) {
            executorService.execute(() -> {
                try {
                    groupFacadeService.joinGroup(testGroupId, user.getUserId(), user.getNickname());
                    successCount.incrementAndGet();
                    System.out.println("✅ 참여 성공: " + user.getNickname());
                } catch (BusinessException e) {
                    failCount.incrementAndGet();
                    System.out.println("❌ 참여 실패: " + user.getNickname() + " - " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        System.out.println("\n=== 비관적 락 적용 결과 ===");
        System.out.println("✅ 성공: " + successCount.get() + "명");
        System.out.println("❌ 실패: " + failCount.get() + "명");

        Group finalGroup = groupService.findById(testGroupId);
        System.out.println("최종 참여자 수: " + finalGroup.getCurrentParticipants() + "/" + finalGroup.getTargetParticipants());

        // 비관적 락으로 정원이 정확히 지켜져야 함
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(9);
        assertThat(finalGroup.getCurrentParticipants()).isEqualTo(3);
        assertThat(finalGroup.getTargetParticipants()).isEqualTo(3);
    }


}
