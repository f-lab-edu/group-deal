package com.app.groupdeal.application.user;

import com.app.groupdeal.application.user.service.UserService;
import com.app.groupdeal.domain.user.User;
import com.app.groupdeal.domain.user.UserRepository;
import com.app.groupdeal.global.error.exception.BusinessException;
import com.app.groupdeal.global.util.PasswordEncoder;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserService 통합테스트")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공")
    void createUser_Success() {
        //given
        String rawPassword = "1234";
        User user = User.builder()
                .email("signup@test.com")
                .password(rawPassword)
                .nickname("회원가입테스트")
                .build();

        //when
        User savedUser = userService.createUser(user);

        //then
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("signup@test.com");
        assertThat(savedUser.getNickname()).isEqualTo("회원가입테스트");

        assertThat(savedUser.getPassword()).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches("1234", savedUser.getPassword())).isTrue();

    }

    @Test
    @DisplayName("이메일이 중복되면 예외 발생")
    void createUser_duplicateEmail_fail(){
        //given

        User user1 = User.builder()
                .email("duple@test.com")
                .nickname("중복테스트1")
                .password("1234")
                .build();
        userService.createUser(user1);

        User user2 = User.builder()
                .email("duple@test.com")
                .nickname("중복테스트2")
                .password("1234")
                .build();

        //when & then
        assertThatThrownBy(() -> userService.createUser(user2))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이메일");

    }

    @Test
    @DisplayName("닉네임이 중복되면 예외 발생")
    void createUser_duplicateNickname_fail(){

        //given
        User user1 = User.builder()
                .email("nickduple1@test.com")
                .nickname("닉중복테스트")
                .password("1234")
                .build();
        userService.createUser(user1);

        User user2 = User.builder()
                .email("nickduple2@test.com")
                .nickname("닉중복테스트")
                .password("1234")
                .build();

        //when & then
        assertThatThrownBy(() -> userService.createUser(user2))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("닉네임");

    }

    @Test
    @DisplayName("로그인 정상 처리")
    void login_success(){

        //given
        User user = User.builder()
                .email("login@test.com")
                .nickname("로그인테스트")
                .password("1234")
                .build();

        userService.createUser(user);

        //when
        User loginUser = userService.login(user.getEmail(), "1234");

        //then
        assertThat(loginUser).isNotNull();
        assertThat(loginUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(loginUser.getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인하면 예외 발생")
    void login_userNotFound_fail(){

        //when & then
        assertThatThrownBy(() -> userService.login("userNotFound@test.com", "1234"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외 발생")
    void login_wrongPassword_fail(){
        //given
        User user = User.builder()
                .email("test@test.com")
                .nickname("테스트")
                .password("1234")
                .build();
        userService.createUser(user);

        //when & then
        assertThatThrownBy(() -> userService.login(user.getEmail(), "wrongPassword"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("비밀번호가 암호화 되어 저장된다")
    void creatUser_password_encrypted(){
        //given
        String rawPassword = "1234";
        User user = User.builder()
                .email("test@test.com")
                .nickname("테스트")
                .password(rawPassword)
                .build();

        //when
        User savedUser = userService.createUser(user);

        //then
        assertThat(savedUser.getPassword()).isNotEqualTo(rawPassword);
        assertThat(savedUser.getPassword()).startsWith("$2a$");
        assertThat(passwordEncoder.matches(rawPassword, savedUser.getPassword())).isTrue();
    }





}
