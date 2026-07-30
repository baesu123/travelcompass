package com.example.travelcompass.common.response;

import com.example.travelcompass.common.exception.ErrorCode;
import lombok.Getter;

// 모든 REST API 응답을 감싸는 공통 포맷 (success/code/message/data)
@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "SUCCESS", "요청이 성공했습니다.", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, "SUCCESS", message, data);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode, T data) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), data);
    }
}
