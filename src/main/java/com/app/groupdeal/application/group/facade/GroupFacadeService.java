package com.app.groupdeal.application.group.facade;

import com.app.groupdeal.application.group.service.GroupService;
import com.app.groupdeal.application.user.service.UserService;
import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.presentation.common.dto.PageResponse;
import com.app.groupdeal.presentation.group.dto.CreateGroupRequestDto;
import com.app.groupdeal.presentation.group.dto.CreateGroupResponseDto;
import com.app.groupdeal.presentation.group.dto.SearchGroupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.app.groupdeal.domain.user.User;


@Service
@RequiredArgsConstructor
public class GroupFacadeService {

    private final UserService userService;
    private final GroupService groupService;

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
}
