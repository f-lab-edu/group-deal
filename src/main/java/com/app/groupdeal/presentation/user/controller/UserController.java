package com.app.groupdeal.presentation.user.controller;

import com.app.groupdeal.application.user.facade.AuthFacadeService;
import com.app.groupdeal.global.error.ErrorType;
import com.app.groupdeal.global.error.exception.BusinessException;
import com.app.groupdeal.presentation.common.dto.ApiResponse;
import com.app.groupdeal.global.session.SessionUser;
import com.app.groupdeal.presentation.user.dto.LoginRequestDto;
import com.app.groupdeal.presentation.user.dto.LoginResponseDto;
import com.app.groupdeal.presentation.user.dto.SignUpRequestDto;
import com.app.groupdeal.presentation.user.dto.SignUpResponseDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

        SessionUser sessionUser = SessionUser.from(
                response.getUserId(),
                response.getEmail(),
                response.getNickname()
        );
        sessionUser.saveToSession(httpSession);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession httpSession){

        SessionUser sessionUser = SessionUser.fromSession(httpSession);


        if (sessionUser == null) {
            throw new BusinessException(ErrorType.UNAUTHORIZED);
        }

        httpSession.invalidate();

        return ResponseEntity.ok(ApiResponse.success());
    }


}
