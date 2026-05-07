package com.swna.server.common.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드 정의
 * 
 * 코드 체계: [도메인]_[번호]
 * - 도메인: COMMON, AUTH, USER, PRODUCT, ORDER, PAYMENT, SALE, STORE, FILE
 * - 번호: 3자리 숫자 (001-999)
 * - 범위: 000-099(COMMON), 100-199(AUTH), 200-299(USER), 300-399(PRODUCT),
 *         400-499(ORDER), 500-599(SALE), 600-699(PAYMENT), 700-799(STORE), 800-899(FILE)
 * 
 * @author SWNA Team
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ================= COMMON (000-099) =================
    INVALID_INPUT("COMMON_001", "Invalid input", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("COMMON_002", "Resource not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACCESS("COMMON_003", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_ACCESS("COMMON_004", "Forbidden access", HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR("COMMON_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_API_ERROR("COMMON_501", "External API error", HttpStatus.BAD_GATEWAY),
    DATABASE_ERROR("COMMON_502", "Database error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_INTEGRITY_VIOLATION("COMMON_503", "Data integrity violation", HttpStatus.CONFLICT),

    // ================= AUTH (100-199) =================
    AUTH_TOKEN_EXPIRED("AUTH_101", "Token expired", HttpStatus.UNAUTHORIZED),
    AUTH_INVALID_TOKEN("AUTH_102", "Invalid token", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_MISSING("AUTH_103", "Token missing", HttpStatus.UNAUTHORIZED),
    AUTH_INVALID_CREDENTIALS("AUTH_104", "Invalid credentials", HttpStatus.UNAUTHORIZED),
    AUTH_ACCOUNT_LOCKED("AUTH_105", "Account locked", HttpStatus.FORBIDDEN),
    AUTH_ACCOUNT_DISABLED("AUTH_106", "Account disabled", HttpStatus.FORBIDDEN),
    AUTH_INVALID_PASSWORD("AUTH_107", "Invalid password", HttpStatus.BAD_REQUEST),

    // ================= USER (200-299) =================
    USER_NOT_FOUND("USER_201", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_202", "User already exists", HttpStatus.CONFLICT),
    USER_INVALID_PASSWORD("USER_203", "Invalid password", HttpStatus.BAD_REQUEST),
    USER_EMAIL_DUPLICATE("USER_204", "Email already exists", HttpStatus.CONFLICT),
    USER_PHONE_DUPLICATE("USER_205", "Phone number already exists", HttpStatus.CONFLICT),
    USER_ALREADY_DELETED("USER_206", "User already deleted", HttpStatus.GONE),
    USER_INVALID_ROLE("USER_207", "Invalid role assigned", HttpStatus.BAD_REQUEST),

    // ================= PRODUCT (300-399) =================
    PRODUCT_NOT_FOUND("PRODUCT_301", "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_ALREADY_EXISTS("PRODUCT_302", "Product already exists", HttpStatus.CONFLICT),
    PRODUCT_BARCODE_DUPLICATE("PRODUCT_303", "Product barcode already exists", HttpStatus.CONFLICT),
    PRODUCT_OUT_OF_STOCK("PRODUCT_304", "Product out of stock", HttpStatus.BAD_REQUEST),
    PRODUCT_PRICE_INVALID("PRODUCT_305", "Invalid product price", HttpStatus.BAD_REQUEST),
    PRODUCT_QUANTITY_INSUFFICIENT("PRODUCT_306", "Insufficient product quantity", HttpStatus.BAD_REQUEST),
    PRODUCT_CATEGORY_NOT_FOUND("PRODUCT_307", "Product category not found", HttpStatus.NOT_FOUND),
    PRODUCT_DISCONTINUED("PRODUCT_308", "Product discontinued", HttpStatus.GONE),

    // ================= ORDER (400-499) =================
    ORDER_NOT_FOUND("ORDER_401", "Order not found", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_CANCELLED("ORDER_402", "Order already cancelled", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_COMPLETED("ORDER_403", "Order already completed", HttpStatus.BAD_REQUEST),
    ORDER_PAYMENT_PENDING("ORDER_404", "Order payment pending", HttpStatus.PAYMENT_REQUIRED),
    ORDER_INVALID_STATUS("ORDER_405", "Invalid order status transition", HttpStatus.BAD_REQUEST),
    ORDER_CANNOT_CANCEL("ORDER_406", "Order cannot be cancelled", HttpStatus.BAD_REQUEST),
    ORDER_EMPTY_ITEMS("ORDER_407", "Order must have at least one item", HttpStatus.BAD_REQUEST),

    // ================= SALE (500-599) =================
    SALE_NOT_FOUND("SALE_501", "Sale not found", HttpStatus.NOT_FOUND),
    SALE_ALREADY_CANCELLED("SALE_502", "Sale already cancelled", HttpStatus.BAD_REQUEST),
    SALE_ALREADY_COMPLETED("SALE_503", "Sale already completed", HttpStatus.BAD_REQUEST),
    SALE_PAYMENT_MISMATCH("SALE_504", "Payment amount mismatch", HttpStatus.BAD_REQUEST),
    SALE_EMPTY_ITEMS("SALE_505", "Sale must have at least one item", HttpStatus.BAD_REQUEST),
    SALE_INVALID_DISCOUNT("SALE_506", "Invalid discount amount", HttpStatus.BAD_REQUEST),
    SALE_ALREADY_REFUNDED("SALE_507", "Sale already refunded", HttpStatus.BAD_REQUEST),

    // 영수증 관련 (508-510)
    RECEIPT_NUMBER_DUPLICATE("SALE_508", "Receipt number already assigned", HttpStatus.CONFLICT),
    INVALID_RECEIPT_NUMBER("SALE_509", "Invalid receipt number format", HttpStatus.BAD_REQUEST),
    RECEIPT_NUMBER_GENERATION_FAILED("SALE_510", "Failed to generate receipt number", HttpStatus.INTERNAL_SERVER_ERROR),

    // 추가 SALE 관련 에러 (필요시 확장)
    SALE_INVALID_STATUS_TRANSITION("SALE_511", "Invalid sale status transition", HttpStatus.BAD_REQUEST),
    SALE_CANNOT_REFUND("SALE_512", "Sale cannot be refunded", HttpStatus.BAD_REQUEST),
    SALE_CANNOT_CANCEL("SALE_513", "Sale cannot be cancelled", HttpStatus.BAD_REQUEST),


    // ================= PAYMENT (600-699) =================
    PAYMENT_FAILED("PAYMENT_601", "Payment failed", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_FOUND("PAYMENT_602", "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_AMOUNT_INVALID("PAYMENT_603", "Invalid payment amount", HttpStatus.BAD_REQUEST),
    PAYMENT_CARD_EXPIRED("PAYMENT_604", "Card has expired", HttpStatus.BAD_REQUEST),
    PAYMENT_CARD_DECLINED("PAYMENT_605", "Card declined", HttpStatus.BAD_REQUEST),
    PAYMENT_INSUFFICIENT_BALANCE("PAYMENT_606", "Insufficient balance", HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_PROCESSED("PAYMENT_607", "Payment already processed", HttpStatus.CONFLICT),
    PAYMENT_GATEWAY_ERROR("PAYMENT_608", "Payment gateway error", HttpStatus.BAD_GATEWAY),

    // ================= STORE (700-799) =================
    STORE_NOT_FOUND("STORE_701", "Store not found", HttpStatus.NOT_FOUND),
    STORE_ALREADY_EXISTS("STORE_702", "Store already exists", HttpStatus.CONFLICT),
    STORE_INACTIVE("STORE_703", "Store is inactive", HttpStatus.FORBIDDEN),
    STORE_ACCESS_DENIED("STORE_704", "Store access denied", HttpStatus.FORBIDDEN),

    // ================= FILE (800-899) =================
    FILE_UPLOAD_FAILED("FILE_801", "File upload failed", HttpStatus.BAD_REQUEST),
    FILE_NOT_FOUND("FILE_802", "File not found", HttpStatus.NOT_FOUND),
    FILE_SIZE_EXCEEDED("FILE_803", "File size exceeded", HttpStatus.BAD_REQUEST),
    FILE_TYPE_NOT_SUPPORTED("FILE_804", "File type not supported", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    /**
     * 동적 메시지 생성 (포맷팅 지원)
     */
    public String formatMessage(Object... args) {
        if (args == null || args.length == 0) {
            return this.message;
        }
        return String.format(this.message, args);
    }

    /**
     * 상세 정보와 함께 예외 생성 (빌더 패턴)
     */
    public BusinessException withDetail(String key, Object value) {
        return BusinessException.builder(this)
            .detail(key, value)
            .build();
    }

    /**
     * 여러 상세 정보와 함께 예외 생성
     */
    public BusinessException withDetails(Map<String, Object> details) {
        return BusinessException.builder(this)
            .details(details)
            .build();
    }

    /**
     * 커스텀 메시지로 예외 생성
     */
    public BusinessException withMessage(String customMessage, Object... args) {
        return BusinessException.builder(this)
            .message(customMessage, args)
            .build();
    }

    /**
     * 기본 예외 생성
     */
    public BusinessException exception() {
        return BusinessException.of(this);
    }

    /**
     * 상태 코드 그룹 확인
     */
    public boolean isClientError() {
        return status.is4xxClientError();
    }

    public boolean isServerError() {
        return status.is5xxServerError();
    }

    public boolean isSuccess() {
        return status.is2xxSuccessful();
    }

    /**
     * 재시도 가능 여부 (서버 에러는 재시도 가능)
     */
    public boolean isRetryable() {
        return isServerError();
    }

    /**
     * 도메인 반환
     */
    public String getDomain() {
        return code.split("_")[0];
    }
}