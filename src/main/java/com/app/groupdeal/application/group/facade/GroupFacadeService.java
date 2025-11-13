package com.app.groupdeal.application.group.facade;

import com.app.groupdeal.application.group.service.GroupService;
import com.app.groupdeal.application.user.service.UserService;
import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.presentation.group.dto.CreateGroupRequestDto;
import com.app.groupdeal.presentation.group.dto.CreateGroupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.app.groupdeal.domain.user.User;


@Service
@RequiredArgsConstructor
public class GroupFacadeService {

    private final UserService userService;
    private final GroupService groupService;

    public CreateGroupResponseDto createGroup(Long userId, CreateGroupRequestDto request) {

        User user = userService.findById(userId);

        Group tempGroup = request.toDomain(userId);

        Group group = groupService.createGroup(tempGroup);

        return CreateGroupResponseDto.of(group, user);


    }
}
