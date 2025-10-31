package com.app.groupdeal.domain.user;

import com.app.groupdeal.domain.common.BaseDomain;
import com.app.groupdeal.infrastructure.user.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class User extends BaseDomain {

    private Long userId;
    private String email;
    private String password;
    private String nickname;

    @Builder
    public User(Long userId, String email, String password, String nickname) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;

    }

    public void encryptPassword(String encryptPassword) {
        this.password = encryptPassword;
    }

    public UserEntity toEntity() {
        return UserEntity.from(this);
    }
}
