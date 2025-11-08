package com.app.groupdeal.application.group.service;


import com.app.groupdeal.domain.group.Group;
import com.app.groupdeal.domain.group.GroupMember;
import com.app.groupdeal.domain.group.GroupMemberRepository;
import com.app.groupdeal.domain.group.GroupRepository;
import lombok.RequiredArgsConstructor;
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
}
