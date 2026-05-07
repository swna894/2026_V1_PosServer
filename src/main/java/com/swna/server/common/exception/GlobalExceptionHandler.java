package com.swna.server.common.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.swna.server.common.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseFactory errorResponseFactory;

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException e,
            HttpServletRequest request) {

        ErrorCode errorCode = e.getErrorCode();

        // 로깅 (에러 레벨 동적 결정)
        if (errorCode.isServerError()) {
            log.error("[{}] Business error: {} - {} | Path: {} | Details: {}",
                errorCode.getCode(), e.getMessage(), errorCode.getMessage(),
                request.getRequestURI(), e.getDetails(), e);
        } else {
            log.warn("[{}] Business error: {} - {} | Path: {} | Details: {}",
                errorCode.getCode(), e.getMessage(), errorCode.getMessage(),
                request.getRequestURI(), e.getDetails());
        }

        ApiResponse<Object> response = errorResponseFactory.createErrorResponse(e, request);

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    /**
     * Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                error -> error.getField(),
                error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                (existing, replacement) -> existing
            ));

        log.warn("Validation failed: {} {} | Errors: {}",
            request.getMethod(), request.getRequestURI(), fieldErrors);

        ApiResponse<Object> response = errorResponseFactory.createValidationErrorResponse(fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 파라미터 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {

        log.warn("Type mismatch: {} {} | Parameter: {} | Expected: {}",
            request.getMethod(), request.getRequestURI(),
            e.getName(), e.getRequiredType());

        String message = String.format("Parameter '%s' must be of type %s",
            e.getName(), e.getRequiredType().getSimpleName());

        return ResponseEntity
            .badRequest()
            .body(errorResponseFactory.createErrorResponse(ErrorCode.INVALID_INPUT, message));
    }

    /**
     * 필수 파라미터 누락 예외 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParameterException(
            MissingServletRequestParameterException e,
            HttpServletRequest request) {

        log.warn("Missing parameter: {} {} | Parameter: {}",
            request.getMethod(), request.getRequestURI(), e.getParameterName());

        String message = String.format("Required parameter '%s' is missing", e.getParameterName());

        return ResponseEntity
            .badRequest()
            .body(errorResponseFactory.createErrorResponse(ErrorCode.INVALID_INPUT, message));
    }

    /**
     * JSON 파싱 예외 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleMessageNotReadableException(
            HttpMessageNotReadableException e,
            HttpServletRequest request) {

        log.warn("Invalid request body: {} {} | Error: {}",
            request.getMethod(), request.getRequestURI(), e.getMessage());

        return ResponseEntity
            .badRequest()
            .body(errorResponseFactory.createErrorResponse(
                ErrorCode.INVALID_INPUT,
                "Invalid request body format"
            ));
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException e,
            HttpServletRequest request) {

        log.warn("Illegal argument: {} {} | Message: {}",
            request.getMethod(), request.getRequestURI(), e.getMessage());

        return ResponseEntity
            .badRequest()
            .body(errorResponseFactory.createErrorResponse(ErrorCode.INVALID_INPUT, e.getMessage()));
    }

    /**
     * 일반 예외 처리 (최종)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(
            Exception e,
            HttpServletRequest request) {

        log.error("Unexpected error: {} {} | Error: {}",
            request.getMethod(), request.getRequestURI(), e.getMessage(), e);

        // 개발 환경에서는 상세 정보 포함
        ApiResponse<Object> response = isDevelopmentProfile() ?
            errorResponseFactory.createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, 
                Map.of("exception", e.getClass().getSimpleName(), "message", e.getMessage())) :
            errorResponseFactory.createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);

        return ResponseEntity.internalServerError().body(response);
    }

    /**
     * 프로필 확인 (application.yml에서 active profile 확인)
     */
    private boolean isDevelopmentProfile() {
        // 실제 구현 시 @Value("${spring.profiles.active}") 사용
        return false;
    }
}