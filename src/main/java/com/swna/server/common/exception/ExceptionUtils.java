package com.swna.server.common.exception;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import lombok.experimental.UtilityClass;

/**
 * 예외 생성을 위한 유틸리티 클래스
 */
@UtilityClass
public class ExceptionUtils {
    
    // ===================== 기본 예외 =====================
    
    /**
     * 기본 비즈니스 예외 생성
     */
    public static BusinessException of(ErrorCode errorCode) {
        return BusinessException.of(errorCode);
    }
    
    /**
     * 커스텀 메시지로 예외 생성
     */
    public static BusinessException of(ErrorCode errorCode, String message, Object... args) {
        return BusinessException.builder(errorCode)
            .message(message, args)
            .build();
    }
    
    /**
     * 상세 정보로 예외 생성
     */
    public static BusinessException of(ErrorCode errorCode, Map<String, Object> details) {
        return BusinessException.builder(errorCode)
            .details(details)
            .build();
    }
    // ===================== AUTH 관련 메서드 =====================
    /**
     * 잘못된 비밀번호
     */
    public static BusinessException invalidPassword() {
        return BusinessException.builder(ErrorCode.AUTH_INVALID_PASSWORD)
            .message("Invalid password")
            .detail("operation", "validatePassword")
            .detail("timestamp", LocalDateTime.now())
            .build();
    }

    /**
     * 잘못된 비밀번호 (상세 정보 포함)
     */
    public static BusinessException invalidPassword(String email) {
        return BusinessException.builder(ErrorCode.AUTH_INVALID_PASSWORD)
            .message("Invalid password for email: %s", email)
            .detail("email", email)
            .detail("operation", "validatePassword")
            .build();
    }
    /**
     * 토큰이 만료됨
     */
    public static BusinessException tokenExpired() {
        return BusinessException.builder(ErrorCode.AUTH_TOKEN_EXPIRED)
            .message("Token has expired")
            .detail("operation", "parseClaims")
            .detail("timestamp", LocalDateTime.now())
            .build();
    }
    
    /**
     * 유효하지 않은 토큰
     */
    public static BusinessException invalidToken() {
        return BusinessException.builder(ErrorCode.AUTH_INVALID_TOKEN)
            .message("Invalid token")
            .detail("operation", "parseClaims")
            .build();
    }
    
    /**
     * 유효하지 않은 토큰 (상세 정보 포함)
     */
    public static BusinessException invalidToken(String reason) {
        return BusinessException.builder(ErrorCode.AUTH_INVALID_TOKEN)
            .message("Invalid token: %s", reason)
            .detail("reason", reason)
            .detail("operation", "parseClaims")
            .build();
    }
    
    /**
     * 토큰이 없음
     */
    public static BusinessException tokenMissing() {
        return BusinessException.builder(ErrorCode.AUTH_TOKEN_MISSING)
            .message("Token is missing")
            .detail("operation", "parseClaims")
            .build();
    }
    
    // ===================== PRODUCT 관련 예외 =====================
    
    /**
     * 상품을 찾을 수 없음
     */
    public static BusinessException productNotFound(String barcode) {
        return BusinessException.builder(ErrorCode.PRODUCT_NOT_FOUND)
            .message("Product not found with barcode: %s", barcode)
            .detail("barcode", barcode)
            .detail("operation", "getProductByBarcode")
            .detail("requestedAt", LocalDateTime.now())
            .build();
    }
    
    /**
     * 상품을 찾을 수 없음 (추가 정보 포함)
     */
    public static BusinessException productNotFound(String barcode, String operation) {
        return BusinessException.builder(ErrorCode.PRODUCT_NOT_FOUND)
            .message("Product not found with barcode: %s", barcode)
            .detail("barcode", barcode)
            .detail("operation", operation)
            .detail("requestedAt", LocalDateTime.now())
            .build();
    }
    
    /**
     * 상품 재고 부족
     */
    public static BusinessException productOutOfStock(String barcode, int requested, int available) {
        return BusinessException.builder(ErrorCode.PRODUCT_OUT_OF_STOCK)
            .message("Out of stock: requested %d, available %d", requested, available)
            .detail("barcode", barcode)
            .detail("requestedQuantity", requested)
            .detail("availableQuantity", available)
            .build();
    }
    
    /**
     * 상품 바코드 중복
     */
    public static BusinessException productBarcodeDuplicate(String barcode) {
        return BusinessException.builder(ErrorCode.PRODUCT_BARCODE_DUPLICATE)
            .message("Product barcode already exists: %s", barcode)
            .detail("barcode", barcode)
            .build();
    }
    
    // ===================== USER 관련 예외 =====================
    
    /**
     * 사용자를 찾을 수 없음
     */
    public static BusinessException userNotFound(String email) {
        return BusinessException.builder(ErrorCode.USER_NOT_FOUND)
            .message("User not found with email: %s", email)
            .detail("email", email)
            .build();
    }
    
    /**
     * 사용자 이미 존재함
     */
    public static BusinessException userAlreadyExists(String email) {
        return BusinessException.builder(ErrorCode.USER_ALREADY_EXISTS)
            .message("User already exists with email: %s", email)
            .detail("email", email)
            .build();
    }
    
    // ===================== INPUT 검증 관련 예외 =====================
    
    /**
     * 필수 입력값 누락
     */
    public static BusinessException missingField(String fieldName) {
        return BusinessException.builder(ErrorCode.INVALID_INPUT)
            .message("%s cannot be empty", fieldName)
            .detail("field", fieldName)
            .build();
    }
    
    /**
     * 잘못된 입력값
     */
    public static BusinessException invalidInput(String fieldName, String reason) {
        return BusinessException.builder(ErrorCode.INVALID_INPUT)
            .message("Invalid %s: %s", fieldName, reason)
            .detail("field", fieldName)
            .detail("reason", reason)
            .build();
    }
    
    /**
     * 잘못된 입력값 (값 포함)
     */
    public static BusinessException invalidInputWithValue(String fieldName, Object value, String reason) {
        return BusinessException.builder(ErrorCode.INVALID_INPUT)
            .message("Invalid %s: '%s' - %s", fieldName, value, reason)
            .detail("field", fieldName)
            .detail("value", value)
            .detail("reason", reason)
            .build();
    }
    
    // ===================== ORDER 관련 예외 =====================
    
    /**
     * 주문을 찾을 수 없음
     */
    public static BusinessException orderNotFound(Long orderId) {
        return BusinessException.builder(ErrorCode.ORDER_NOT_FOUND)
            .message("Order not found with id: %d", orderId)
            .detail("orderId", orderId)
            .build();
    }
    
    // ===================== COMMON 예외 =====================
    
    /**
     * 리소스를 찾을 수 없음
     */
    public static BusinessException resourceNotFound(String resourceType, String identifier) {
        return BusinessException.builder(ErrorCode.RESOURCE_NOT_FOUND)
            .message("%s not found: %s", resourceType, identifier)
            .detail("resourceType", resourceType)
            .detail("identifier", identifier)
            .build();
    }
    
    /**
     * 내부 서버 에러
     */
    public static BusinessException internalError(String message, Object... args) {
        return BusinessException.builder(ErrorCode.INTERNAL_SERVER_ERROR)
            .message(message, args)
            .build();
    }

        
    // ===================== RECEIPT 관련 예외 (추가) =====================
    
    /**
     * 영수증 번호 중복
     */
    public static BusinessException receiptNumberDuplicate(String receiptNo) {
        return BusinessException.builder(ErrorCode.RECEIPT_NUMBER_DUPLICATE)
            .message("Receipt number already assigned: %s", receiptNo)
            .detail("receiptNumber", receiptNo)
            .detail("operation", "assignReceiptNo")
            .detail("timestamp", LocalDateTime.now())
            .build();
    }
    
    /**
     * 잘못된 영수증 번호 형식
     */
    public static BusinessException invalidReceiptNumber(String receiptNo) {
        return BusinessException.builder(ErrorCode.INVALID_RECEIPT_NUMBER)
            .message("Invalid receipt number format: %s", receiptNo)
            .detail("receiptNumber", receiptNo)
            .detail("expectedFormat", "RCP + YYYYMMDDHHMMSS + 4~8자리 영문/숫자")
            .detail("timestamp", LocalDateTime.now())
            .build();
    }
    
    /**
     * 영수증 번호 생성 실패
     */
    public static BusinessException receiptNumberGenerationFailed(String reason) {
        return BusinessException.builder(ErrorCode.RECEIPT_NUMBER_GENERATION_FAILED)
            .message("Failed to generate receipt number: %s", reason)
            .detail("reason", reason)
            .detail("operation", "generateReceiptNumber")
            .detail("timestamp", LocalDateTime.now())
            .build();
    }
}