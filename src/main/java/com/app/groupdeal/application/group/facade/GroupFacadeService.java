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
@Transactional(readOnly = true)
public class GroupFacadeService {

    private final UserService userService;
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;

    @Transactional
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

        List<GroupMember> joinedMembers = groupMemberService.findJoinedMembers(groupId);

        return DetailGroupResponseDto.of(group, joinedMembers);


    }

    @Transactional
    public JoinGroupResponseDto joinGroup(Long groupId, Long userId, String nickname) {

        Group group = groupService.findByIdWithLock(groupId);

        if(group.getHostMemberId().equals(userId)) {
            throw new BusinessException(ErrorType.CANNOT_JOIN_OWN_GROUP);
        }

        group.validateJoinable();

        GroupMember joinedGroupMember = groupMemberService.joinGroup(groupId, userId, nickname);

        groupService.increaseParticipant(groupId);

        Group joinedGroup = groupService.findById(groupId);

        return JoinGroupResponseDto.of(joinedGroup, joinedGroupMember);
    }

    @Transactional
    public LeaveGroupResponseDto leaveGroup(Long groupId, Long userId){

        Group group = groupService.findById(groupId);

        group.validateLeavable();

        GroupMember member = groupMemberService.findByGroupMember(groupId, userId);

        if(member.isHost()) {
            throw new BusinessException(ErrorType.HOST_CANNOT_LEAVE);
        }

        if(member.isLeft()) {
            throw new BusinessException(ErrorType.ALREADY_LEFT);
        }

        GroupMember leftGroupMember = groupMemberService.leaveGroup(groupId, userId);

        groupService.decreaseParticipant(groupId);

        Group leftGroup = groupService.findById(groupId);

        return LeaveGroupResponseDto.of(leftGroup, leftGroupMember);
    }
}
