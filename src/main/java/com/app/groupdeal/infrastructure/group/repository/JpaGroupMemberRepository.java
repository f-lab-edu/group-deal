package com.app.groupdeal.infrastructure.group.repository;

import com.app.groupdeal.domain.group.constants.GroupMemberStatus;
import com.app.groupdeal.domain.group.model.GroupMember;
import com.app.groupdeal.domain.group.repository.GroupMemberRepository;
import com.app.groupdeal.infrastructure.group.entity.GroupMemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public List<GroupMember> findByGroupIdAndStatus(Long groupId, GroupMemberStatus status) {
        return groupMemberEntityRepository.findByGroupIdAndGroupMemberStatus(groupId, status).stream()
                .map(GroupMemberEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteAll(){
        groupMemberEntityRepository.deleteAll();
    }

    @Override
    public boolean existsByGroupIdAndUserIdAndStatus(Long groupId, Long userId, GroupMemberStatus status) {
        return groupMemberEntityRepository.existsByGroupIdAndUserIdAndGroupMemberStatus(groupId, userId, status);
    }

    @Override
    public Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId) {
        return groupMemberEntityRepository.findByGroupIdAndUserId(groupId, userId)
                .map(GroupMemberEntity::toDomain);
    }


    interface GroupMemberEntityRepository extends JpaRepository<GroupMemberEntity, Long> {

        List<GroupMemberEntity> findByGroupIdAndGroupMemberStatus(Long groupId, GroupMemberStatus status);

        boolean existsByGroupIdAndUserIdAndGroupMemberStatus(Long groupId, Long userId, GroupMemberStatus status);

        Optional<GroupMemberEntity> findByGroupIdAndUserId(Long groupId, Long userId);
    }

}
