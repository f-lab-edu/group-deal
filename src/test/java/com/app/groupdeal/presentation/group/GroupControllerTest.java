package com.app.groupdeal.presentation.group;

import com.app.groupdeal.application.group.service.GroupService;
import com.app.groupdeal.application.user.service.UserService;
import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.repository.GroupMemberRepository;
import com.app.groupdeal.domain.group.repository.GroupRepository;
import com.app.groupdeal.domain.user.User;
import com.app.groupdeal.domain.user.UserRepository;
import com.app.groupdeal.presentation.group.dto.CreateGroupRequestDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("GroupController 통합테스트")
@ActiveProfiles("test")
class GroupControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;


    private String sessionId;
    private Long userId;

    // 참여/나가기 테스트용
    private String hostSessionId;
    private String memberSessionId;
    private Long hostUserId;
    private Long memberUserId;
    private Long testGroupId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        userRepository.deleteAll();
        groupRepository.deleteAll();
        groupMemberRepository.deleteAll();

        // 기존 테스트용 사용자 생성 및 로그인
        User user = User.builder()
                .email("test@test.com")
                .password("1234")
                .nickname("테스트유저")
                .build();
        User savedUser = userService.createUser(user);
        userId = savedUser.getUserId();

        sessionId = given()
                .contentType(ContentType.JSON)
                .body(new com.app.groupdeal.presentation.user.dto.LoginRequestDto(
                        "test@test.com",
                        "1234"
                ))
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .cookie("JSESSIONID");

        // 참여/나가기 테스트용 호스트 생성
        User hostUser = User.builder()
                .email("host@test.com")
                .password("1234")
                .nickname("호스트")
                .build();
        User savedHostUser = userService.createUser(hostUser);
        hostUserId = savedHostUser.getUserId();

        hostSessionId = given()
                .contentType(ContentType.JSON)
                .body(new com.app.groupdeal.presentation.user.dto.LoginRequestDto(
                        "host@test.com",
                        "1234"
                ))
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .cookie("JSESSIONID");

        // 참여/나가기 테스트용 일반 사용자 생성
        User memberUser = User.builder()
                .email("member@test.com")
                .password("1234")
                .nickname("참여자")
                .build();
        User savedMemberUser = userService.createUser(memberUser);
        memberUserId = savedMemberUser.getUserId();

        memberSessionId = given()
                .contentType(ContentType.JSON)
                .body(new com.app.groupdeal.presentation.user.dto.LoginRequestDto(
                        "member@test.com",
                        "1234"
                ))
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .cookie("JSESSIONID");

        // 테스트용 그룹 생성
        Group testGroup = Group.create(
                "코스트코 견과류 3kg",
                "식품",
                "신선한 견과류입니다",
                "500g씩",
                30000,
                6,
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
    @DisplayName("그룹 생성 API 정상 처리")
    void createGroup_Success() {
        // given
        CreateGroupRequestDto request = CreateGroupRequestDto.builder()
                .productName("코스트코 견과류 3kg")
                .category("식품")
                .description("신선한 견과류입니다")
                .dividedUnit("500g씩")
                .originalPrice(30000)
                .targetParticipants(6)
                .recruitmentMinutes(1440)
                .meetingLocation("강남역 3번 출구")
                .meetingAt(LocalDateTime.now().plusDays(3))
                .build();

        // when & then
        given()
                .cookie("JSESSIONID", sessionId)
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/v1/groups")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.groupId", notNullValue())
                .body("data.productName", equalTo("코스트코 견과류 3kg"))
                .body("data.category", equalTo("식품"))
                .body("data.originalPrice", equalTo(30000))
                .body("data.targetParticipants", equalTo(6))
                .body("data.currentParticipants", equalTo(1))
                .body("data.status", equalTo("RECRUITING"))
                .body("data.hostMemberId", equalTo(userId.intValue()))
                .body("data.hostMemberName", equalTo("테스트유저"));
    }

    @Test
    @DisplayName("로그인하지 않은 상태에서 그룹 생성 시 실패")
    void createGroup_WithoutLogin_Fail() {
        // given
        CreateGroupRequestDto request = CreateGroupRequestDto.builder()
                .productName("테스트 상품")
                .category("식품")
                .originalPrice(10000)
                .targetParticipants(5)
                .recruitmentMinutes(1440)
                .meetingLocation("강남역")
                .meetingAt(LocalDateTime.now().plusDays(3))
                .build();

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/v1/groups")
        .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("그룹 목록 조회 API 정상 처리")
    void searchGroup_Success() {
        // given
        for (int i = 1; i <= 15; i++) {
            Group group = Group.create(
                    "상품" + i, "식품", "설명" + i, "단위", 10000 * i,
                    5, 1440, "강남역", LocalDateTime.now().plusDays(2),
                    userId, "테스트유저"
            );
            groupService.createGroup(group);
        }

        // when & then
        given()
        .when()
                .get("/api/v1/groups?page=0&size=10")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.content", hasSize(10))
                .body("data.totalElements", equalTo(16))
                .body("data.totalPages", equalTo(2))
                .body("data.page", equalTo(0))
                .body("data.size", equalTo(10))
                .body("data.first", equalTo(true))
                .body("data.last", equalTo(false))
                .body("data.hasNext", equalTo(true));
    }

    @Test
    @DisplayName("그룹 상세 조회 API 정상 처리")
    void getGroupDetail_Success() {
        // given
        Group group = Group.create(
                "코스트코 견과류", "식품", "신선한 견과류입니다", "500g씩",
                30000, 6, 1440, "강남역 3번 출구",
                LocalDateTime.now().plusDays(2),
                userId, "테스트유저"
        );
        Group savedGroup = groupService.createGroup(group);

        // when & then
        given()
        .when()
                .get("/api/v1/groups/" + savedGroup.getGroupId())
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.groupId", equalTo(savedGroup.getGroupId().intValue()))
                .body("data.productName", equalTo("코스트코 견과류"))
                .body("data.category", equalTo("식품"))
                .body("data.description", equalTo("신선한 견과류입니다"))
                .body("data.originalPrice", equalTo(30000))
                .body("data.targetParticipants", equalTo(6))
                .body("data.currentParticipants", equalTo(1))
                .body("data.status", equalTo("RECRUITING"))
                .body("data.host.userId", equalTo(userId.intValue()))
                .body("data.host.nickname", equalTo("테스트유저"))
                .body("data.members", hasSize(1))
                .body("data.members[0].userId", equalTo(userId.intValue()))
                .body("data.members[0].nickname", equalTo("테스트유저"))
                .body("data.members[0].memberType", equalTo("HOST"))
                .body("data.members[0].joinedAt", notNullValue())
                .body("data.progressRate", notNullValue())
                .body("data.remainingMinutes", notNullValue());
    }

    @Test
    @DisplayName("그룹 생성 시 호스트가 자동으로 참여자에 추가됨")
    void createGroup_HostAutoAddedAsMember() {
        // given
        CreateGroupRequestDto request = CreateGroupRequestDto.builder()
                .productName("테스트 상품")
                .category("식품")
                .originalPrice(10000)
                .targetParticipants(5)
                .recruitmentMinutes(1440)
                .meetingLocation("강남역")
                .meetingAt(LocalDateTime.now().plusDays(3))
                .build();

        // when
        String groupId = given()  // ✅ String으로 받음
                .cookie("JSESSIONID", sessionId)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/groups")
                .then()
                .statusCode(200)
                .extract()
                .path("data.groupId")
                .toString();

        // then - 상세 조회로 참여자 확인
        given()
                .when()
                .get("/api/v1/groups/" + groupId)
                .then()
                .statusCode(200)
                .body("data.members", hasSize(1))
                .body("data.members[0].userId", equalTo(userId.intValue()))
                .body("data.members[0].memberType", equalTo("HOST"));
    }

    @Test
    @DisplayName("존재하지 않는 그룹 조회 시 실패")
    void getGroupDetail_NotFound_Fail() {
        // when & then
        given()
        .when()
                .get("/api/v1/groups/999999")
        .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("그룹 목록이 최신순으로 정렬되는지 확인")
    void searchGroup_OrderByCreatedTimeDesc() {
        // given
        Group group1 = Group.create(
                "첫 번째 상품", "식품", "설명1", "단위",
                10000, 5, 1440, "강남역",
                LocalDateTime.now().plusDays(2),
                userId, "테스트유저"
        );
        groupService.createGroup(group1);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Group group2 = Group.create(
                "두 번째 상품", "식품", "설명2", "단위",
                20000, 5, 1440, "신촌역",
                LocalDateTime.now().plusDays(2),
                userId, "테스트유저"
        );
        groupService.createGroup(group2);

        // when & then
        given()
        .when()
                .get("/api/v1/groups?page=0&size=10")
        .then()
                .statusCode(200)
                .body("data.content[0].productName", equalTo("두 번째 상품"))
                .body("data.content[1].productName", equalTo("첫 번째 상품"));
    }

    @Test
    @DisplayName("그룹 참여 API 정상 처리")
    void joinGroup_Success() {
        // when & then
        given()
                .cookie("JSESSIONID", memberSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/join")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.groupId", equalTo(testGroupId.intValue()))
                .body("data.productName", equalTo("코스트코 견과류 3kg"))
                .body("data.currentParticipants", equalTo(2))
                .body("data.targetParticipants", equalTo(6))
                .body("data.status", equalTo("RECRUITING"))
                .body("data.joinMember.userId", equalTo(memberUserId.intValue()))
                .body("data.joinMember.nickname", equalTo("참여자"))
                .body("data.joinMember.memberType", equalTo("MEMBER"))
                .body("data.joinMember.joinedAt", notNullValue());
    }

    @Test
    @DisplayName("로그인하지 않은 상태에서 그룹 참여 시 실패")
    void joinGroup_WithoutLogin_Fail() {
        // when & then
        given()
                .when()
                .post("/api/v1/groups/" + testGroupId + "/join")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("자신이 생성한 그룹에 참여 시도 시 실패")
    void joinGroup_OwnGroup_Fail() {
        // when & then
        given()
                .cookie("JSESSIONID", hostSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/join")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("이미 참여한 그룹에 재참여 시도 시 실패")
    void joinGroup_AlreadyJoined_Fail() {
        // given - 먼저 참여
        given()
                .cookie("JSESSIONID", memberSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/join")
                .then()
                .statusCode(200);

        // when & then - 재참여 시도
        given()
                .cookie("JSESSIONID", memberSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/join")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("그룹 나가기 API 정상 처리")
    void leaveGroup_Success() {
        // given - 먼저 참여
        given()
                .cookie("JSESSIONID", memberSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/join")
                .then()
                .statusCode(200);

        // when & then - 나가기
        given()
                .cookie("JSESSIONID", memberSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/leave")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.groupId", equalTo(testGroupId.intValue()))
                .body("data.productName", equalTo("코스트코 견과류 3kg"))
                .body("data.currentParticipants", equalTo(1))
                .body("data.targetParticipants", equalTo(6))
                .body("data.status", equalTo("RECRUITING"))
                .body("data.leaveMember.userId", equalTo(memberUserId.intValue()))
                .body("data.leaveMember.nickname", equalTo("참여자"))
                .body("data.leaveMember.memberType", equalTo("MEMBER"))
                .body("data.leaveMember.leftAt", notNullValue());
    }

    @Test
    @DisplayName("로그인하지 않은 상태에서 그룹 나가기 시 실패")
    void leaveGroup_WithoutLogin_Fail() {
        // when & then
        given()
                .when()
                .post("/api/v1/groups/" + testGroupId + "/leave")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("호스트가 그룹 나가기 시도 시 실패")
    void leaveGroup_Host_Fail() {
        // when & then
        given()
                .cookie("JSESSIONID", hostSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/leave")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("이미 나간 그룹 다시 나가기 시도 시 실패")
    void leaveGroup_AlreadyLeft_Fail() {
        // given - 참여 후 나가기
        given()
                .cookie("JSESSIONID", memberSessionId)
                .post("/api/v1/groups/" + testGroupId + "/join");

        given()
                .cookie("JSESSIONID", memberSessionId)
                .post("/api/v1/groups/" + testGroupId + "/leave");

        // when & then - 다시 나가기 시도
        given()
                .cookie("JSESSIONID", memberSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/leave")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("참여하지 않은 그룹 나가기 시도 시 실패")
    void leaveGroup_NotJoined_Fail() {
        // when & then
        given()
                .cookie("JSESSIONID", memberSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/leave")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("그룹 재참여 시나리오 - 나갔다가 다시 참여")
    void rejoinGroup_Scenario() {
        // 1. 참여
        given()
                .cookie("JSESSIONID", memberSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/join")
                .then()
                .statusCode(200)
                .body("data.currentParticipants", equalTo(2))
                .body("data.joinMember.userId", equalTo(memberUserId.intValue()))
                .body("data.joinMember.joinedAt", notNullValue());

        // 2. 나가기
        given()
                .cookie("JSESSIONID", memberSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/leave")
                .then()
                .statusCode(200)
                .body("data.currentParticipants", equalTo(1))
                .body("data.leaveMember.userId", equalTo(memberUserId.intValue()))
                .body("data.leaveMember.leftAt", notNullValue());

        // 3. 재참여
        given()
                .cookie("JSESSIONID", memberSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/join")
                .then()
                .statusCode(200)
                .body("data.currentParticipants", equalTo(2))
                .body("data.joinMember.userId", equalTo(memberUserId.intValue()))
                .body("data.joinMember.joinedAt", notNullValue());
    }

    @Test
    @DisplayName("그룹 참여 후 상세 조회 시 멤버 목록에 포함됨")
    void joinGroup_CheckMemberList() {
        // given - 참여
        given()
                .cookie("JSESSIONID", memberSessionId)
                .when()
                .post("/api/v1/groups/" + testGroupId + "/join")
                .then()
                .statusCode(200);

        // when & then - 상세 조회로 멤버 확인
        given()
                .when()
                .get("/api/v1/groups/" + testGroupId)
                .then()
                .statusCode(200)
                .body("data.members", hasSize(2))
                .body("data.members[0].memberType", equalTo("HOST"))
                .body("data.members[1].userId", equalTo(memberUserId.intValue()))
                .body("data.members[1].nickname", equalTo("참여자"))
                .body("data.members[1].memberType", equalTo("MEMBER"))
                .body("data.currentParticipants", equalTo(2));
    }

    @Test
    @DisplayName("그룹 나가기 후 상세 조회 시 멤버 목록에서 제외됨")
    void leaveGroup_CheckMemberList() {
        // given - 참여 후 나가기
        given()
                .cookie("JSESSIONID", memberSessionId)
                .post("/api/v1/groups/" + testGroupId + "/join");

        given()
                .cookie("JSESSIONID", memberSessionId)
                .post("/api/v1/groups/" + testGroupId + "/leave");

        // when & then - 상세 조회로 멤버 확인
        given()
                .when()
                .get("/api/v1/groups/" + testGroupId)
                .then()
                .statusCode(200)
                .body("data.members", hasSize(1))
                .body("data.members[0].userId", equalTo(hostUserId.intValue()))
                .body("data.members[0].memberType", equalTo("HOST"))
                .body("data.currentParticipants", equalTo(1));
    }


}
