package com.app.groupdeal.infrastructure.group;

import com.app.groupdeal.domain.group.Group;
import com.app.groupdeal.domain.group.GroupRepository;
import lombok.RequiredArgsConstructor;
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

    interface GroupEntityRepository extends JpaRepository<GroupEntity, Long> {

    }
}
