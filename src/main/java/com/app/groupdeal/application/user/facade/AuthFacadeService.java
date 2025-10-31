package com.app.groupdeal.application.user.facade;

import com.app.groupdeal.application.user.service.UserService;
import com.app.groupdeal.domain.user.User;
import com.app.groupdeal.presentation.user.dto.LoginRequestDto;
import com.app.groupdeal.presentation.user.dto.LoginResponseDto;
import com.app.groupdeal.presentation.user.dto.SignUpRequestDto;
import com.app.groupdeal.presentation.user.dto.SignUpResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthFacadeService {

    private final UserService userService;

    @Transactional
    public SignUpResponseDto signUp(SignUpRequestDto request) {
        User user = request.toDomain();

        User savedUser = userService.createUser(user);

        return SignUpResponseDto.of(savedUser);
    }


    public LoginResponseDto login(String email, String password) {

        User loginUser = userService.login(email, password);

        return LoginResponseDto.of(loginUser);
    }
}
