package com.swna.server.common.response;

import java.time.LocalDateTime;

import com.swna.server.common.exception.ErrorCode;

public record ApiResponse<T>(
        boolean success,
        T data,
        String code,
        String message,
        LocalDateTime timestamp
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(
                false,
                null,
                errorCode.getCode(),
                errorCode.getMessage(),
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>(
                false,
                null,
                errorCode.getCode(),
                message,
                LocalDateTime.now()
        );
    }
}

