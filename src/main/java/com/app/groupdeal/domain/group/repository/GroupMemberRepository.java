package com.app.groupdeal.domain.group.repository;

import com.app.groupdeal.domain.group.model.GroupMember;

public interface GroupMemberRepository {

    GroupMember save(GroupMember groupMember);
}
