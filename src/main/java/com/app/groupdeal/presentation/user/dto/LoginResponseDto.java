package com.app.groupdeal.presentation.user.dto;

import com.app.groupdeal.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {

    private Long userId;
    private String email;
    private String nickname;

    public static LoginResponseDto of(User user) {
        return LoginResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
