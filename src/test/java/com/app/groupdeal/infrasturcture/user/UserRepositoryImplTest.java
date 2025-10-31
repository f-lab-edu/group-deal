//package com.app.groupdeal.infrasturcture.user;
//
//import com.app.groupdeal.domain.user.User;
//import com.app.groupdeal.domain.user.UserRepository;
//import com.app.groupdeal.infrastructure.user.UserRepositoryImpl;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@ActiveProfiles("test")
//@Import(UserRepositoryImpl.class)
//@Transactional
//class UserRepositoryImplTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    @DisplayName("사용자 저장 성공")
//    void save(){
//
//        //given
//        User user = User.builder()
//                .email("test@test.com")
//                .password("1234")
//                .nickname("테스트")
//                .build();
//
//        //when
//        User savedUser = userRepository.save(user);
//
//        //then
//        assertThat(savedUser.getUserId()).isNotNull();
//        assertThat(savedUser.getEmail()).isEqualTo("test@test.com");
//        assertThat(savedUser.getNickname()).isEqualTo("테스트");
//    }
//
//    @Test
//    @DisplayName("이메일로 사용자 조회 성공")
//    void findByEmail_Success(){
//
//        //given
//        User user = User.builder()
//                .email("test@test.com")
//                .password("1234")
//                .nickname("테스트")
//                .build();
//        userRepository.save(user);
//
//        //when
//        Optional<User> targetUser = userRepository.findByEmail("test@test.com");
//
//        //then
//        assertThat(targetUser).isPresent();
//        assertThat(targetUser.get().getEmail()).isEqualTo("test@test.com");
//    }
//
//    @Test
//    @DisplayName("이메일 중복 체크")
//    void existsByEmail_True(){
//
//        //given
//        User user = User.builder()
//                .email("test@test.com")
//                .password("1234")
//                .nickname("테스트")
//                .build();
//        userRepository.save(user);
//
//        //when
//        boolean existsEmail = userRepository.existsByEmail("test@test.com");
//
//        //then
//        assertThat(existsEmail).isTrue();
//    }
//
//    @Test
//    @DisplayName("닉네임 중복 체크 - 존재")
//    void existsByNickname_True(){
//
//        // given
//        User user = User.builder()
//                .email("test@test.com")
//                .password("encodedPassword")
//                .nickname("테스트")
//                .build();
//        userRepository.save(user);
//
//        // when
//        boolean existNickname = userRepository.existsByNickname("테스트");
//
//        // then
//        assertThat(existNickname).isTrue();
//
//    }
//
//}
