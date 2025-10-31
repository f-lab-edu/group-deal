package com.app.groupdeal.domain.user;

public interface UserRepository {

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
