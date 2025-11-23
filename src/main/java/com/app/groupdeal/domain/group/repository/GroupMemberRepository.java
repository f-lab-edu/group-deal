package com.app.groupdeal.domain.group.repository;

import com.app.groupdeal.domain.group.model.GroupMember;

import java.util.List;

public interface GroupMemberRepository {

    GroupMember save(GroupMember groupMember);

    List<GroupMember> findByGroupId(Long groupId);

    void deleteAll();
}
