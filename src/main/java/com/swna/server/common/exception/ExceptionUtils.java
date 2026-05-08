package com.swna.server.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.experimental.UtilityClass;

/**
 * 예외 생성을 위한 유틸리티 클래스
 */
@UtilityClass
public class ExceptionUtils {

    // ===================== 상수 정의 (Literal Constants) =====================
    private static final String KEY_OPERATION = "operation";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_REASON = "reason";
    private static final String KEY_PARSE_CLAIMS = "parseClaims";
    
    // 추가 추출 상수
    private static final String KEY_FIELD = "field";
    private static final String KEY_VALUE = "value";
    private static final String KEY_ORDER_ID = "orderId";
    private static final String KEY_RECEIPT_NUMBER = "receiptNumber";
    private static final String KEY_REQUESTED_AT = "requestedAt";

    // ===================== 기본 예외 =====================
    
    public static BusinessException of(ErrorCode errorCode) {
        return BusinessException.of(errorCode);
    }
    
    public static BusinessException of(ErrorCode errorCode, String message, Object... args) {
        return BusinessException.builder(errorCode)
            .message(message, args)
            .build();
    }
    
    public static BusinessException of(ErrorCode errorCode, Map<String, Object> details) {
        return BusinessException.builder(errorCode)
            .details(details)
            .build();
    }

    // ===================== AUTH 관련 메서드 =====================

    public static BusinessException invalidPassword() {
        return BusinessException.builder(ErrorCode.AUTH_INVALID_PASSWORD)
            .message("Invalid password")
            .detail(KEY_OPERATION, "validatePassword")
            .detail(KEY_TIMESTAMP, LocalDateTime.now())
            .build();
    }

    public static BusinessException invalidPassword(String email) {
        return BusinessException.builder(ErrorCode.AUTH_INVALID_PASSWORD)
            .message("Invalid password for email: %s", email)
            .detail(KEY_EMAIL, email)
            .detail(KEY_OPERATION, "validatePassword")
            .build();
    }

    public static BusinessException tokenExpired() {
        return BusinessException.builder(ErrorCode.AUTH_TOKEN_EXPIRED)
            .message("Token has expired")
            .detail(KEY_OPERATION, KEY_PARSE_CLAIMS)
            .detail(KEY_TIMESTAMP, LocalDateTime.now())
            .build();
    }
    
    public static BusinessException invalidToken() {
        return BusinessException.builder(ErrorCode.AUTH_INVALID_TOKEN)
            .message("Invalid token")
            .detail(KEY_OPERATION, KEY_PARSE_CLAIMS)
            .build();
    }
    
    public static BusinessException invalidToken(String reason) {
        return BusinessException.builder(ErrorCode.AUTH_INVALID_TOKEN)
            .message("Invalid token: %s", reason)
            .detail(KEY_REASON, reason)
            .detail(KEY_OPERATION, KEY_PARSE_CLAIMS)
            .build();
    }
    
    public static BusinessException tokenMissing() {
        return BusinessException.builder(ErrorCode.AUTH_TOKEN_MISSING)
            .message("Token is missing")
            .detail(KEY_OPERATION, KEY_PARSE_CLAIMS)
            .build();
    }
    
    // ===================== PRODUCT 관련 예외 =====================
    
    public static BusinessException productNotFound(String barcode) {
        return BusinessException.builder(ErrorCode.PRODUCT_NOT_FOUND)
            .message("Product not found with barcode: %s", barcode)
            .detail(KEY_BARCODE, barcode)
            .detail(KEY_OPERATION, "getProductByBarcode")
            .detail(KEY_REQUESTED_AT, LocalDateTime.now())
            .build();
    }
    
    public static BusinessException productNotFound(String barcode, String operation) {
        return BusinessException.builder(ErrorCode.PRODUCT_NOT_FOUND)
            .message("Product not found with barcode: %s", barcode)
            .detail(KEY_BARCODE, barcode)
            .detail(KEY_OPERATION, operation)
            .detail(KEY_REQUESTED_AT, LocalDateTime.now())
            .build();
    }
    
    public static BusinessException productOutOfStock(String barcode, int requested, int available) {
        return BusinessException.builder(ErrorCode.PRODUCT_OUT_OF_STOCK)
            .message("Out of stock: requested %d, available %d", requested, available)
            .detail(KEY_BARCODE, barcode)
            .detail("requestedQuantity", requested)
            .detail("availableQuantity", available)
            .build();
    }
    
    public static BusinessException productBarcodeDuplicate(String barcode) {
        return BusinessException.builder(ErrorCode.PRODUCT_BARCODE_DUPLICATE)
            .message("Product barcode already exists: %s", barcode)
            .detail(KEY_BARCODE, barcode)
            .build();
    }
    
    // ===================== USER 관련 예외 =====================
    
    public static BusinessException userNotFound(String email) {
        return BusinessException.builder(ErrorCode.USER_NOT_FOUND)
            .message("User not found with email: %s", email)
            .detail(KEY_EMAIL, email)
            .build();
    }
    
    public static BusinessException userAlreadyExists(String email) {
        return BusinessException.builder(ErrorCode.USER_ALREADY_EXISTS)
            .message("User already exists with email: %s", email)
            .detail(KEY_EMAIL, email)
            .build();
    }
    
    // ===================== INPUT 검증 관련 예외 =====================
    
    public static BusinessException missingField(String fieldName) {
        return BusinessException.builder(ErrorCode.INVALID_INPUT)
            .message("%s cannot be empty", fieldName)
            .detail(KEY_FIELD, fieldName)
            .build();
    }
    
    public static BusinessException invalidInput(String fieldName, String reason) {
        return BusinessException.builder(ErrorCode.INVALID_INPUT)
            .message("Invalid %s: %s", fieldName, reason)
            .detail(KEY_FIELD, fieldName)
            .detail(KEY_REASON, reason)
            .build();
    }
    
    public static BusinessException invalidInputWithValue(String fieldName, Object value, String reason) {
        return BusinessException.builder(ErrorCode.INVALID_INPUT)
            .message("Invalid %s: '%s' - %s", fieldName, value, reason)
            .detail(KEY_FIELD, fieldName)
            .detail(KEY_VALUE, value)
            .detail(KEY_REASON, reason)
            .build();
    }
    
    // ===================== ORDER 관련 예외 =====================
    
    public static BusinessException orderNotFound(Long orderId) {
        return BusinessException.builder(ErrorCode.ORDER_NOT_FOUND)
            .message("Order not found with id: %d", orderId)
            .detail(KEY_ORDER_ID, orderId)
            .build();
    }
    
    // ===================== COMMON 예외 =====================
    
    public static BusinessException resourceNotFound(String resourceType, String identifier) {
        return BusinessException.builder(ErrorCode.RESOURCE_NOT_FOUND)
            .message("%s not found: %s", resourceType, identifier)
            .detail("resourceType", resourceType)
            .detail("identifier", identifier)
            .build();
    }
    
    public static BusinessException internalError(String message, Object... args) {
        return BusinessException.builder(ErrorCode.INTERNAL_SERVER_ERROR)
            .message(message, args)
            .build();
    }

    // ===================== RECEIPT 관련 예외 =====================
    
    public static BusinessException receiptNumberDuplicate(String receiptNo) {
        return BusinessException.builder(ErrorCode.RECEIPT_NUMBER_DUPLICATE)
            .message("Receipt number already assigned: %s", receiptNo)
            .detail(KEY_RECEIPT_NUMBER, receiptNo)
            .detail(KEY_OPERATION, "assignReceiptNo")
            .detail(KEY_TIMESTAMP, LocalDateTime.now())
            .build();
    }
    
    public static BusinessException invalidReceiptNumber(String receiptNo) {
        return BusinessException.builder(ErrorCode.INVALID_RECEIPT_NUMBER)
            .message("Invalid receipt number format: %s", receiptNo)
            .detail(KEY_RECEIPT_NUMBER, receiptNo)
            .detail("expectedFormat", "RCP + YYYYMMDDHHMMSS + 4~8자리 영문/숫자")
            .detail(KEY_TIMESTAMP, LocalDateTime.now())
            .build();
    }
    
    public static BusinessException receiptNumberGenerationFailed(String reason) {
        return BusinessException.builder(ErrorCode.RECEIPT_NUMBER_GENERATION_FAILED)
            .message("Failed to generate receipt number: %s", reason)
            .detail(KEY_REASON, reason)
            .detail(KEY_OPERATION, "generateReceiptNumber")
            .detail(KEY_TIMESTAMP, LocalDateTime.now())
            .build();
    }
}