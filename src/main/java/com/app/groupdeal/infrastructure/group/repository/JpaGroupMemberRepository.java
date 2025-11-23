package com.app.groupdeal.infrastructure.group.repository;

import com.app.groupdeal.domain.group.model.GroupMember;
import com.app.groupdeal.domain.group.repository.GroupMemberRepository;
import com.app.groupdeal.infrastructure.group.entity.GroupMemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Override
    public List<GroupMember> findByGroupId(Long groupId) {
        return groupMemberEntityRepository.findByGroupId(groupId).stream()
                .map(GroupMemberEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteAll(){
        groupMemberEntityRepository.deleteAll();
    }

    @Override
    public boolean existsByGroupIdAndUserId(Long groupId, Long userId) {
        return groupMemberEntityRepository.existsByGroupIdAndUserId(groupId, userId);
    }


    interface GroupMemberEntityRepository extends JpaRepository<GroupMemberEntity, Long> {

        List<GroupMemberEntity> findByGroupId(Long groupId);

        boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    }

}
