package com.app.groupdeal.application.group.service;

import com.app.groupdeal.domain.group.constants.GroupMemberStatus;
import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.model.GroupMember;
import com.app.groupdeal.domain.group.repository.GroupMemberRepository;
import com.app.groupdeal.global.error.ErrorType;
import com.app.groupdeal.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;

    public List<GroupMember> findJoinedMembers(Long groupId) {
        return groupMemberRepository.findByGroupIdAndStatus(groupId, GroupMemberStatus.JOINED);
    }

    public boolean isAlreadyJoined(Long groupId, Long userId) {
        return groupMemberRepository.existsByGroupIdAndUserIdAndStatus(groupId, userId, GroupMemberStatus.JOINED);
    }

    @Transactional
    public GroupMember joinGroup(Long groupId, Long userId, String nickname) {

        Optional<GroupMember> existingMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);

        if (existingMember.isPresent() && existingMember.get().getGroupMemberStatus() == GroupMemberStatus.LEFT) {

            GroupMember member = existingMember.get();
            member.joinGroup();
            return groupMemberRepository.save(member);
        }

        GroupMember member = GroupMember.createMember(groupId, userId, nickname);

        try {
            return groupMemberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorType.ALREADY_JOINED);
        }
    }

    @Transactional
    public GroupMember joinGroupWithINCR(Long groupId, Long userId, String nickname, Integer queueNumber) {

        Optional<GroupMember> existingMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);

        if (existingMember.isPresent() && existingMember.get().getGroupMemberStatus() == GroupMemberStatus.LEFT) {

            GroupMember member = existingMember.get();
            member.rejoinGroup(queueNumber);
            return groupMemberRepository.save(member);
        }

        GroupMember member = GroupMember.createMemberWithQueue(groupId, userId, nickname, queueNumber);

        try {
            return groupMemberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorType.ALREADY_JOINED);
        }
    }

    public GroupMember findByGroupMember(Long groupId, Long userId) {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException(ErrorType.NOT_FOUND, "그룹 멤버"));
    }

    @Transactional
    public GroupMember leaveGroup(Long groupId, Long userId) {
        GroupMember groupMember = findByGroupMember(groupId, userId);
        groupMember.leaveGroup();
        return groupMemberRepository.save(groupMember);
    }

}
