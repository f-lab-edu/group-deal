package com.app.groupdeal.infrastructure.user;

import com.app.groupdeal.domain.user.User;
import com.app.groupdeal.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserEntityRepository userEntityRepository;

    @Override
    public User save(User user) {

        UserEntity userEntity = user.toEntity();

        UserEntity savedUserEntity = userEntityRepository.save(userEntity);

        return savedUserEntity.toDomain();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userEntityRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userEntityRepository.existsByNickname(nickname);
    }

}
