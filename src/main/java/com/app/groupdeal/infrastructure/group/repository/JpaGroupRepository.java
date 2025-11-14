package com.app.groupdeal.infrastructure.group.repository;

import com.app.groupdeal.domain.group.model.Group;
import com.app.groupdeal.domain.group.repository.GroupRepository;
import com.app.groupdeal.infrastructure.group.entity.GroupEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
        return null;
    }

    interface GroupEntityRepository extends JpaRepository<GroupEntity, Long> {

    }
}
