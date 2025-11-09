package com.app.groupdeal.infrastructure.group.repository;

import com.app.groupdeal.domain.group.model.GroupMember;
import com.app.groupdeal.domain.group.repository.GroupMemberRepository;
import com.app.groupdeal.infrastructure.group.entity.GroupMemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaGroupMemberRepository implements GroupMemberRepository {

    private final GroupMemberEntityRepository groupMemberEntityRepository;

    @Override
    public GroupMember save(GroupMember groupMember) {

        GroupMemberEntity groupMemberEntity = GroupMemberEntity.from(groupMember);
        GroupMemberEntity savedGroupMemberEntity = groupMemberEntityRepository.save(groupMemberEntity);

        return savedGroupMemberEntity.toDomain();


    }

    interface GroupMemberEntityRepository extends JpaRepository<GroupMemberEntity, Long> {

    }

}
