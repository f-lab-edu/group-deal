package com.app.groupdeal.application.group.facade;

import com.app.groupdeal.application.group.service.GroupMemberService;
import com.app.groupdeal.application.group.service.GroupService;
import com.app.groupdeal.application.user.service.UserService;
import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.model.GroupMember;
import com.app.groupdeal.global.error.ErrorType;
import com.app.groupdeal.global.error.exception.BusinessException;
import com.app.groupdeal.presentation.common.dto.PageResponse;
import com.app.groupdeal.presentation.group.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.app.groupdeal.domain.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GroupFacadeService {

    private final UserService userService;
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;

    public CreateGroupResponseDto createGroup(Long userId, CreateGroupRequestDto request) {

        User user = userService.findById(userId);

        Group tempGroup = request.toDomain(user.getUserId(), user.getNickname());

        Group group = groupService.createGroup(tempGroup);

        return CreateGroupResponseDto.of(group);


    }

    public PageResponse<SearchGroupResponseDto> searchGroup(int page, int size) {

        Page<Group> groupPage = groupService.searchGroup(page, size);

        Page<SearchGroupResponseDto> responsePage = groupPage.map(SearchGroupResponseDto::of);

        return PageResponse.of(responsePage);
    }

    public DetailGroupResponseDto getDetailGroup(Long groupId) {

        Group group = groupService.findById(groupId);

        List<GroupMember> groupMembers = groupMemberService.findByGroupId(groupId);

        return DetailGroupResponseDto.of(group, groupMembers);


    }

    @Transactional
    public JoinGroupResponseDto joinGroup(Long groupId, Long userId, String nickname) {

        Group group = groupService.findById(groupId);

        if(group.getHostMemberId().equals(userId)) {
            throw new BusinessException(ErrorType.CANNOT_JOIN_OWN_GROUP);
        }

        group.validateJoinable();

        if (groupMemberService.isAlreadyJoined(groupId, userId)) {
            throw new BusinessException(ErrorType.ALREADY_JOINED);
        }

        GroupMember updatedGroupMember = groupMemberService.joinGroup(groupId, userId, nickname);

        groupService.increaseParticipant(groupId);

        Group updatedGroup = groupService.findById(groupId);

        return JoinGroupResponseDto.of(updatedGroup, updatedGroupMember);
    }
}
