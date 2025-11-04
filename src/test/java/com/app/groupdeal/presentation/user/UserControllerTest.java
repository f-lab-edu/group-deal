package com.app.groupdeal.presentation.user;

import com.app.groupdeal.application.user.service.UserService;
import com.app.groupdeal.domain.user.User;
import com.app.groupdeal.presentation.user.dto.LoginRequestDto;
import com.app.groupdeal.presentation.user.dto.SignUpRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@DisplayName("UserController 통합테스트")
@ActiveProfiles("test")
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("회원가입 API 정상 처리")
    void signup_success() {

        //given
        SignUpRequestDto request = new SignUpRequestDto(
                "test@test.com",
                "1234",
                "테스트"
        );

        //when & then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/v1/auth/signup")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.email", equalTo("test@test.com"))
                .body("data.nickname", equalTo("테스트"))
                .body("data.userId", notNullValue());
    }

    @Test
    @DisplayName("회원가입시 이메일이 중복되면 예외가 발생한다.")
    void signup_dupleEmail_fail(){
        //given
        SignUpRequestDto request1 = new SignUpRequestDto(
                "test@test.com",
                "1234",
                "테스트"
        );

        given()
                .contentType(ContentType.JSON)
                .body(request1)
        .when()
                .post("/api/v1/auth/signup")
        .then()
                .statusCode(200);

        SignUpRequestDto request2 = new SignUpRequestDto(
                "test@test.com",
                "1234",
                "테스트"
        );

        //when & then
        given()
                .contentType(ContentType.JSON)
                .body(request2)
        .when()
                .post("/api/v1/auth/signup")
        .then()
                .statusCode(anyOf(is(400), is(409)));
    }


    @Test
    @DisplayName("로그인 API 정상 처리")
    void login_success() {
        //given
        User user = User.builder()
                .email("test@test.com")
                .password("1234")
                .nickname("테스트")
                .build();
        userService.createUser(user);

        LoginRequestDto request = new LoginRequestDto(
                "test@test.com",
                "1234"
        );

        //when & then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/v1/auth/login")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.email", equalTo("test@test.com"))
                .body("data.nickname", equalTo("테스트"))
                .body("data.userId", notNullValue())
                .cookie("JSESSIONID", notNullValue());

    }

    @Test
    @DisplayName("로그인시 존재하지 않는 이메일의 경우 로그인 실패")
    void login_notUserEmail_fail() {
        //given
        LoginRequestDto request = new LoginRequestDto(
                "test@test.com",
                "1234"
        );

        //when & then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/v1/auth/login")
        .then()
                .statusCode(anyOf(is(400), is(409)));
    }

    @Test
    @DisplayName("로그인시 비밀번호가 일치하지 않으면 로그인 실패")
    void login_wrongPassword_fail() {
        //given
        User user = User.builder()
                .email("test@test.com")
                .password("1234")
                .nickname("테스트")
                .build();
        userService.createUser(user);

        LoginRequestDto request = new LoginRequestDto(
                "test@test.com",
                "wrongPassword"
        );

        //when & then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/v1/auth/login")
        .then()
                .statusCode(anyOf(is(400), is(401)));

    }

    @Test
    @DisplayName("로그아웃 API 정상 처리")
    void logout_success() {

        //given
        User user = User.builder()
                .email("test@test.com")
                .nickname("테스트")
                .password("1234")
                .build();
        userService.createUser(user);

        LoginRequestDto request = new LoginRequestDto(
                "test@test.com",
                "1234"
        );

        String sessionId = given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/v1/auth/login")
        .then()
                .statusCode(200)
                .extract()
                .cookie("JSESSIONID");

        //when & then
        given()
                .cookie("JSESSIONID", sessionId)
        .when()
                .post("/api/v1/auth/logout")
        .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("로그인 하지 않은 상태에서 로그아웃 시도 시 예외 발생")
    void logout_withoutLogin_fail(){
        given()
                .when()
                .post("/api/v1/auth/logout")
                .then()
                .statusCode(anyOf(is(400), is(401)));
    }

}
