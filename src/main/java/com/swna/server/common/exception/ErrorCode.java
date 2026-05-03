package com.swna.server.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
/*
   AUTH_XXX     → 인증/인가
   USER_XXX     → 사용자
   ORDER_XXX    → 주문
   PAYMENT_XXX  → 결제
*/
@Getter
@RequiredArgsConstructor
public enum ErrorCode {


    // ================= COMMON =================
    INVALID_INPUT("COMMON_400", "Invalid input", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("COMMON_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    // ================= AUTH =================
    AUTH_TOKEN_EXPIRED("AUTH_1001", "Token expired", HttpStatus.UNAUTHORIZED),
    AUTH_INVALID_TOKEN("AUTH_1002", "Invalid token", HttpStatus.UNAUTHORIZED),
    AUTH_UNAUTHORIZED("AUTH_1003", "Unauthorized", HttpStatus.UNAUTHORIZED),
    AUTH_INVALID_PASSWORD("AUTH_1004", "Invalid password", HttpStatus.BAD_REQUEST),

    // ================= USER =================
    USER_NOT_FOUND("USER_2001", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_2002", "User already exists", HttpStatus.BAD_REQUEST),
    USER_INVALID_PASSWORD("USER_2003", "Invalid password", HttpStatus.BAD_REQUEST),

    // ================= USER =================
    PRODUCT_NOT_FOUND("PRODUCT_2001", "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_ALREADY_EXISTS("PRODUCT_2002", "Product already exists", HttpStatus.BAD_REQUEST),

    // ================= ORDER =================
    ORDER_NOT_FOUND("ORDER_3001", "Order not found", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_CANCELLED("ORDER_3002", "Order already cancelled", HttpStatus.BAD_REQUEST),

    // ================= PAYMENT =================
    PAYMENT_FAILED("PAYMENT_4001", "Payment failed", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_FOUND("PAYMENT_4002", "Payment not found", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    
}