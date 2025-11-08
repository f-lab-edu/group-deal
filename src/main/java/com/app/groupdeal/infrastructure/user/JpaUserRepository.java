package com.app.groupdeal.infrastructure.user;

import com.app.groupdeal.domain.user.User;
import com.app.groupdeal.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final UserEntityRepository userEntityRepository;

    @Override
    public User save(User user) {

        UserEntity userEntity = UserEntity.from(user);
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

    @Override
    public Optional<User> findByEmail(String email) {
        return userEntityRepository.findByEmail(email)
                .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userEntityRepository.findById(id)
                .map(UserEntity::toDomain);

    }

    @Override
    public void deleteAll(){
        userEntityRepository.deleteAll();
    }

    interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
        boolean existsByEmail(String email);
        boolean existsByNickname(String nickname);
        Optional<UserEntity> findByEmail(String email);
    }
}
