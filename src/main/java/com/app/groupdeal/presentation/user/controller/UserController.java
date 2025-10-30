package com.app.groupdeal.presentation.user.controller;

import com.app.groupdeal.application.user.facade.LoginService;
import com.app.groupdeal.presentation.common.dto.ApiResponse;
import com.app.groupdeal.presentation.user.dto.SignUpRequestDto;
import com.app.groupdeal.presentation.user.dto.SignUpResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final LoginService loginService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(@Valid @RequestBody SignUpRequestDto request){
        SignUpResponseDto response = loginService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
