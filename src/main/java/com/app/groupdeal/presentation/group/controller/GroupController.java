package com.app.groupdeal.presentation.group.controller;

import com.app.groupdeal.application.group.facade.GroupFacadeService;
import com.app.groupdeal.global.session.LoginUser;
import com.app.groupdeal.global.session.SessionUser;
import com.app.groupdeal.presentation.common.dto.ApiResponse;
import com.app.groupdeal.presentation.group.dto.CreateGroupRequestDto;
import com.app.groupdeal.presentation.group.dto.CreateGroupResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupFacadeService groupFacadeService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateGroupResponseDto>> createGroup(
            @LoginUser SessionUser sessionUser,
            @Valid @RequestBody CreateGroupRequestDto request) {
        CreateGroupResponseDto response = groupFacadeService.createGroup(sessionUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }



}
