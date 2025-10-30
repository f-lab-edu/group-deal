package com.app.groupdeal.application.user.facade;

import com.app.groupdeal.application.user.service.UserService;
import com.app.groupdeal.presentation.user.dto.SignUpRequestDto;
import com.app.groupdeal.presentation.user.dto.SignUpResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    public SignUpResponseDto signUp(@Valid SignUpRequestDto request) {
        return null;
    }
}
