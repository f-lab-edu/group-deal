package com.app.groupdeal.application.user;

import com.app.groupdeal.application.user.service.UserService;
import com.app.groupdeal.domain.user.User;
import com.app.groupdeal.domain.user.UserRepository;
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
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공")
    void createUser_Success() {
        //given
        String rawPassword = "1234";
        User user = User.builder()
                .email("test@test.com")
                .password(rawPassword)
                .nickname("테스트")
                .build();

        //when
        User savedUser = userService.createUser(user);

        //then
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@test.com");
        assertThat(savedUser.getNickname()).isEqualTo("테스트");

        assertThat(savedUser.getPassword()).isNotEqualTo(rawPassword);
        assertThat(savedUser.getPassword()).startsWith("$2a$");  // BCrypt 형식



    }


}
