package com.swna.server.common.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String customMessage;
    private final Map<String, Object> details;
    private final LocalDateTime timestamp;
    private final String traceId;

    private BusinessException(ErrorCode errorCode, String customMessage, 
                              Map<String, Object> details, String traceId) {
        super(customMessage != null ? customMessage : errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = customMessage;
        this.details = details != null ? Map.copyOf(details) : Map.of();
        this.timestamp = LocalDateTime.now();
        this.traceId = traceId;
    }

    // ===================== Static Factory Methods =====================

    /**
     * 간단한 예외 생성
     */
    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode, null, null, null);
    }

    /**
     * 커스텀 메시지로 예외 생성
     */
    public static BusinessException of(ErrorCode errorCode, String customMessage) {
        return new BusinessException(errorCode, customMessage, null, null);
    }

    /**
     * 상세 정보를 포함한 예외 생성
     */
    public static BusinessException of(ErrorCode errorCode, Map<String, Object> details) {
        return new BusinessException(errorCode, null, details, null);
    }

    /**
     * Builder 패턴 시작
     */
    public static BusinessExceptionBuilder builder(ErrorCode errorCode) {
        return new BusinessExceptionBuilder(errorCode);
    }

    // ===================== Convenience Methods =====================

    /**
     * 상세 정보 조회 (타입 안전)
     */
    @SuppressWarnings("unchecked")
    public <T> T getDetail(String key) {
        return (T) details.get(key);
    }

    /**
     * 상세 정보 존재 여부
     */
    public boolean hasDetail(String key) {
        return details.containsKey(key);
    }

    /**
     * 상세 정보가 비어있는지 여부
     */
    public boolean hasDetails() {
        return !details.isEmpty();
    }

    /**
     * 포맷된 타임스탬프
     */
    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // ===================== Builder Pattern =====================

    public static class BusinessExceptionBuilder {
        private final ErrorCode errorCode;
        private String customMessage;
        private final Map<String, Object> details = new HashMap<>();
        private String traceId;

        public BusinessExceptionBuilder(ErrorCode errorCode) {
            this.errorCode = errorCode;
        }

        public BusinessExceptionBuilder message(String message, Object... args) {
            this.customMessage = (args != null && args.length > 0) ? 
                String.format(message, args) : message;
            return this;
        }

        public BusinessExceptionBuilder detail(String key, Object value) {
            if (key != null && value != null) {
                this.details.put(key, value);
            }
            return this;
        }

        public BusinessExceptionBuilder details(Map<String, Object> details) {
            if (details != null) {
                this.details.putAll(details);
            }
            return this;
        }

        public BusinessExceptionBuilder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public BusinessException build() {
            return new BusinessException(errorCode, customMessage, details, traceId);
        }
    }

    // ===================== Object Methods =====================

    @Override
    public String toString() {
        return String.format("BusinessException{code=%s, message=%s, details=%s, timestamp=%s, traceId=%s}",
            errorCode.getCode(), getMessage(), details, timestamp, traceId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusinessException that)) return false;
        return errorCode == that.errorCode;
    }

    @Override
    public int hashCode() {
        return errorCode.hashCode();
    }
}