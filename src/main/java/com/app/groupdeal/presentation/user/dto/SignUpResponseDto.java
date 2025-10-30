package com.app.groupdeal.presentation.user.dto;

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
}
