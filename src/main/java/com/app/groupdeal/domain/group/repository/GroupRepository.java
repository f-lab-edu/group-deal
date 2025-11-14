package com.app.groupdeal.domain.group.repository;

import com.app.groupdeal.domain.group.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupRepository {
    Group save(Group group);

    Page<Group> findAll(Pageable pageable);
}
