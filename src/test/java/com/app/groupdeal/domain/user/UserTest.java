package com.app.groupdeal.domain.user;

import com.app.groupdeal.global.util.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private final PasswordEncoder passwordEncoder = new PasswordEncoder();

    @Test
    @DisplayName("비밀번호 암호화 후 일치하는지 확인")
    void encryptPassword() {

        //given
        String rawPassword = "1234";
        User user = User.builder()
                .email("test@test.com")
                .password(rawPassword)
                .nickname("테스트")
                .build();

        // when
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.encryptPassword(encryptedPassword);

        //then
        assertThat(user.getPassword()).isEqualTo(encryptedPassword);
        assertThat(user.getPassword()).isNotEqualTo(rawPassword);
    }

    @Test
    @DisplayName("비밀번호가 일치하면 true 반환")
    void isPasswordMatch_Success() {

        //given
        String rawPassword = "1234";
        String encryptedPassword = passwordEncoder.encode(rawPassword);

        User user = User.builder()
                .email("test@test.com")
                .password(encryptedPassword)
                .nickname("테스트")
                .build();

        //when
        boolean result = user.isPasswordMatch(rawPassword, passwordEncoder);

        //then
        assertThat(result).isTrue();
    }
}
