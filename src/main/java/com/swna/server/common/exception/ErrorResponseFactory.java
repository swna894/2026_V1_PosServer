package com.swna.server.common.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.swna.server.common.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ErrorResponseFactory {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    /**
     * 기본 에러 응답 생성
     */
    public ApiResponse<Object> createErrorResponse(ErrorCode errorCode) {
        return ApiResponse.error(errorCode);
    }
    
    /**
     * 커스텀 메시지가 있는 에러 응답
     */
    public ApiResponse<Object> createErrorResponse(ErrorCode errorCode, String customMessage) {
        return ApiResponse.error(errorCode, customMessage);
    }
    
    /**
     * 상세 정보가 있는 에러 응답
     */
    public ApiResponse<Object> createErrorResponse(ErrorCode errorCode, Map<String, Object> details) {
        return ApiResponse.errorWithDetails(errorCode, details);
    }
    
    /**
     * BusinessException으로부터 에러 응답 생성
     */
    public ApiResponse<Object> createErrorResponse(BusinessException e) {
        Map<String, Object> details = new HashMap<>(e.getDetails());
        
        if (e.getTraceId() != null) {
            details.put("traceId", e.getTraceId());
        }
        if (!details.containsKey("timestamp")) {
            details.put("timestamp", e.getFormattedTimestamp());
        }
        
        String message = e.getCustomMessage() != null ? 
            e.getCustomMessage() : e.getErrorCode().getMessage();
        
        return ApiResponse.<Object>builder()
            .success(false)
            .code(e.getErrorCode().getCode())
            .message(message)
            .details(details)
            .build();
    }
    
    /**
     * HttpServletRequest 정보를 포함한 에러 응답
     */
    public ApiResponse<Object> createErrorResponse(ErrorCode errorCode, HttpServletRequest request) {
        Map<String, Object> details = buildRequestDetails(request);
        return ApiResponse.errorWithDetails(errorCode, details);
    }
    
    /**
     * BusinessException + HttpServletRequest 정보를 포함한 에러 응답
     */
    public ApiResponse<Object> createErrorResponse(BusinessException e, HttpServletRequest request) {
        Map<String, Object> details = new HashMap<>(e.getDetails());
        details.putAll(buildRequestDetails(request));
        
        if (e.getTraceId() != null) {
            details.put("traceId", e.getTraceId());
        }
        if (!details.containsKey("timestamp")) {
            details.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));
        }
        
        String message = e.getCustomMessage() != null ? 
            e.getCustomMessage() : e.getErrorCode().getMessage();
        
        return ApiResponse.<Object>builder()
            .success(false)
            .code(e.getErrorCode().getCode())
            .message(message)
            .details(details)
            .build();
    }
    
    /**
     * Validation 에러 응답
     */
    public ApiResponse<Object> createValidationErrorResponse(Map<String, String> fieldErrors) {
        Map<String, Object> details = new HashMap<>();
        details.put("validationErrors", fieldErrors);
        details.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));
        details.put("errorCount", fieldErrors.size());
        
        return ApiResponse.errorWithDetails(ErrorCode.INVALID_INPUT, details);
    }
    
    /**
     * 요청 정보构建
     */
    private Map<String, Object> buildRequestDetails(HttpServletRequest request) {
        Map<String, Object> details = new HashMap<>();
        details.put("path", request.getRequestURI());
        details.put("method", request.getMethod());
        details.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));
        
        // 클라이언트 IP 추가 (선택적)
        String clientIp = getClientIp(request);
        if (clientIp != null) {
            details.put("clientIp", clientIp);
        }
        
        return details;
    }
    
    /**
     * 클라이언트 IP 추출
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}