package com.app.groupdeal.presentation.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final T data;

    public ApiResponse(boolean success) {
        this.success = success;
        this.data = null;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true);
    }
}
