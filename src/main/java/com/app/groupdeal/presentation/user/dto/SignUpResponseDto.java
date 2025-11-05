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
public class SignUpResponseDto {

    private Long userId;
    private String email;
    private String nickname;

    public static SignUpResponseDto of(User user) {
        return SignUpResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
