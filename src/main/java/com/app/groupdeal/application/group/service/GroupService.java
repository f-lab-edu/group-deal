package com.app.groupdeal.application.group.service;


import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.model.GroupMember;
import com.app.groupdeal.domain.group.repository.GroupMemberRepository;
import com.app.groupdeal.domain.group.repository.GroupRepository;
import com.app.groupdeal.global.error.ErrorType;
import com.app.groupdeal.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public Group createGroup(Group group) {

        Group savedGroup = groupRepository.save(group);

        GroupMember hostMember = GroupMember.createHost(savedGroup.getGroupId(), savedGroup.getHostMemberId());
        groupMemberRepository.save(hostMember);

        return savedGroup;



    }

    public Page<Group> searchGroup(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
        return groupRepository.findAll(pageable);
    }

    public Group findById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorType.NOT_FOUND, "그룹"));
    }
}
