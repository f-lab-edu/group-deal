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

        validateDuplicatedEmail(user.getEmail());

        validateDuplicatedNickname(user.getNickname());

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.encryptPassword(encryptedPassword);

        return userRepository.save(user);
    }


    public User login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorType.INVALID_LOGIN_CREDENTIALS));

        if(!user.isPasswordMatch(password, passwordEncoder)) {
            throw new BusinessException(ErrorType.INVALID_LOGIN_CREDENTIALS);
        }

        return user;
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
