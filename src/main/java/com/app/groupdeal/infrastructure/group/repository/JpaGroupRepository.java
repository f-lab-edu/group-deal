package com.app.groupdeal.infrastructure.group.repository;

import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.repository.GroupRepository;
import com.app.groupdeal.infrastructure.group.entity.GroupEntity;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaGroupRepository implements GroupRepository {

    private final GroupEntityRepository groupEntityRepository;

    @Override
    public Group save(Group group) {

        GroupEntity groupEntity = GroupEntity.from(group);
        GroupEntity savedGroupEntity = groupEntityRepository.save(groupEntity);

        return savedGroupEntity.toDomain();


    }

    @Override
    public Page<Group> findAll(Pageable pageable) {
        return groupEntityRepository.findAll(pageable).map(GroupEntity::toDomain);
    }

    @Override
    public Optional<Group> findById(Long groupId) {
        return groupEntityRepository.findById(groupId).map(GroupEntity::toDomain);
    }

    @Override
    public Optional<Group> findByIdWithLock(Long groupId) {
        return groupEntityRepository.findByIdWithLock(groupId)
                .map(GroupEntity::toDomain);
    }

    @Override
    public void deleteAll(){
        groupEntityRepository.deleteAll();
    }

    interface GroupEntityRepository extends JpaRepository<GroupEntity, Long> {
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT g FROM GroupEntity g WHERE g.groupId = :groupId")
        Optional<GroupEntity> findByIdWithLock(@Param("groupId") Long groupId);

    }
}
