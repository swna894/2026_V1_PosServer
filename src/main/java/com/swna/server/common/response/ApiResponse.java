package com.swna.server.common.response;

import java.util.Map;

import com.swna.server.common.exception.ErrorCode;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {
    private final boolean success;
    private final String code;
    private final String message;
    private final T data;
    private final Map<String, Object> details;
    
    // 성공 응답
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    // 에러 응답
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }
    
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(customMessage)
                .build();
    }
    
    public static <T> ApiResponse<T> errorWithDetails(ErrorCode errorCode, Map<String, Object> details) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .details(details)
                .build();
    }
}