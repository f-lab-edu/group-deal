package com.app.groupdeal.application.group.service;

import com.app.groupdeal.domain.group.model.GroupMember;
import com.app.groupdeal.domain.group.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;

    public List<GroupMember> findByGroupId(Long groupId) {
        return groupMemberRepository.findByGroupId(groupId);
    }

    public boolean isAlreadyJoined(Long groupId, Long userId) {
        return groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    @Transactional
    public GroupMember joinGroup(Long groupId, Long userId, String nickname) {
        GroupMember member = GroupMember.createMember(groupId, userId, nickname);
        return groupMemberRepository.save(member);
    }
}
