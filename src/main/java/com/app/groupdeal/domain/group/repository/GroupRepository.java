package com.app.groupdeal.domain.group.repository;

import com.app.groupdeal.domain.group.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    Group save(Group group);

    Page<Group> findAll(Pageable pageable);

    List<Group> findAll();

    Optional<Group> findById(Long groupId);

    Optional<Group> findByIdWithLock(Long groupId);

    void deleteAll();
}
