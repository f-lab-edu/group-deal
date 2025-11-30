package com.app.groupdeal.domain.group.repository;

import com.app.groupdeal.domain.group.constants.GroupMemberStatus;
import com.app.groupdeal.domain.group.model.GroupMember;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository {

    GroupMember save(GroupMember groupMember);

    List<GroupMember> findByGroupIdAndStatus(Long groupId, GroupMemberStatus groupMemberStatus);

    void deleteAll();

    boolean existsByGroupIdAndUserIdAndStatus(Long groupId, Long userId, GroupMemberStatus groupMemberStatus);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

}
