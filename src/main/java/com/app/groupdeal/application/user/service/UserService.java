package com.app.groupdeal.application.user.service;

import com.app.groupdeal.domain.user.User;
import com.app.groupdeal.domain.user.UserRepository;
import com.app.groupdeal.global.error.ErrorType;
import com.app.groupdeal.global.error.exception.BusinessException;
import com.app.groupdeal.global.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user) {

        // 이메일 중복 체크
        validateDuplicatedEmail(user.getEmail());

        // 닉네임 중복 체크
        validateDuplicatedNickname(user.getNickname());

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.encryptPassword(encryptedPassword);

        return userRepository.save(user);
    }

    private void validateDuplicatedEmail(String email) {
        if(userRepository.existsByEmail(email)){
            throw new BusinessException(ErrorType.DUPLICATE_USER_EMAIL);
        }
    }


    private void validateDuplicatedNickname(String nickname) {
        if(userRepository.existsByNickname(nickname)){
            throw new BusinessException(ErrorType.DUPLICATE_USER_NICKNAME);
        }
    }
}
