package com.app.groupdeal.presentation.user.controller;

import com.app.groupdeal.application.user.facade.AuthFacadeService;
import com.app.groupdeal.global.error.ErrorType;
import com.app.groupdeal.global.error.exception.BusinessException;
import com.app.groupdeal.presentation.common.dto.ApiResponse;
import com.app.groupdeal.presentation.user.dto.LoginRequestDto;
import com.app.groupdeal.presentation.user.dto.LoginResponseDto;
import com.app.groupdeal.presentation.user.dto.SignUpRequestDto;
import com.app.groupdeal.presentation.user.dto.SignUpResponseDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserController {

    private final AuthFacadeService authFacadeService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(@Valid @RequestBody SignUpRequestDto request){
        SignUpResponseDto response = authFacadeService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request, HttpSession httpSession){

        LoginResponseDto response = authFacadeService.login(request.getEmail(), request.getPassword());

        httpSession.setAttribute("userId", response.getUserId());
        httpSession.setAttribute("email", response.getEmail());
        httpSession.setAttribute("nickname", response.getNickname());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession httpSession){

        Long userId = (Long) httpSession.getAttribute("userId");

        if (userId == null) {
            throw new BusinessException(ErrorType.UNAUTHORIZED);
        }

        httpSession.invalidate();

        return ResponseEntity.ok(ApiResponse.success());
    }


}
