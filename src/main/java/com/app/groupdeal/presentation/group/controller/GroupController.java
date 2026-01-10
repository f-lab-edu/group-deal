package com.app.groupdeal.presentation.group.controller;

import com.app.groupdeal.application.group.facade.GroupFacadeService;
import com.app.groupdeal.global.session.LoginUser;
import com.app.groupdeal.global.session.SessionUser;
import com.app.groupdeal.presentation.common.dto.ApiResponse;
import com.app.groupdeal.presentation.common.dto.PageResponse;
import com.app.groupdeal.presentation.group.dto.*;
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

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SearchGroupResponseDto>>> searchGroup(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size){
        PageResponse<SearchGroupResponseDto> response = groupFacadeService.searchGroup(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<DetailGroupResponseDto>> getDetailGroup(@PathVariable Long groupId){
        DetailGroupResponseDto response = groupFacadeService.getDetailGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiResponse<JoinGroupResponseDto>> joinGroup(
            @PathVariable Long groupId,
            @LoginUser SessionUser sessionUser){
        JoinGroupResponseDto response = groupFacadeService.joinGroup(groupId, sessionUser.getUserId(), sessionUser.getNickname());
        return ResponseEntity.ok(ApiResponse.success(response));

    }

    @PostMapping("/{groupId}/leave")
    public ResponseEntity<ApiResponse<LeaveGroupResponseDto>> leaveGroup(
            @PathVariable Long groupId,
            @LoginUser SessionUser sessionUser){
        LeaveGroupResponseDto response = groupFacadeService.leaveGroup(groupId, sessionUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    //테스트용
    @PostMapping("/{groupId}/join-test")
    public ResponseEntity<ApiResponse<JoinGroupResponseDto>> joinGroupTest(
            @PathVariable Long groupId,
            @RequestParam Long userId,
            @RequestParam String nickname){
        JoinGroupResponseDto response = groupFacadeService.joinGroupWithEvent(groupId, userId, nickname);
        return ResponseEntity.ok(ApiResponse.success(response));

    }


}
