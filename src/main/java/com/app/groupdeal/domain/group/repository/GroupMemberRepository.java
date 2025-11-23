package com.app.groupdeal.domain.group.repository;

import com.app.groupdeal.domain.group.model.GroupMember;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository {

    GroupMember save(GroupMember groupMember);

    List<GroupMember> findByGroupId(Long groupId);

    void deleteAll();

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
}
