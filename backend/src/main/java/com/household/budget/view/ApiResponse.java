package com.household.budget.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MVC 패턴 - View 계층
 * API 응답을 표준화하는 Response 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "성공", data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, "실패", null, error);
    }

    public static <T> ApiResponse<T> error(String message, String error) {
        return new ApiResponse<>(false, message, null, error);
    }
}


