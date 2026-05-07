package com.swna.server.sale.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.swna.server.sale.entity.CardPaymentEntity;
import com.swna.server.sale.entity.CashPaymentEntity;
import com.swna.server.sale.entity.PaymentEntity;
import com.swna.server.sale.entity.PaymentType;

import lombok.Builder;


@Builder
public record PaymentResponse(
        Long id,
        PaymentType type,
        BigDecimal amount,
        BigDecimal receivedAmount,  // CASH 전용
        BigDecimal changeAmount,    // CASH 전용 (거스름돈)
        String cardNumber           // CARD 전용
) {

    // =========================
    // Factory Methods
    // =========================
    
    /**
     * PaymentEntity로부터 Response 생성 (다형성 처리)
     */
    public static PaymentResponse from(PaymentEntity payment) {
        return switch (payment) {
            case CashPaymentEntity cash -> new PaymentResponse(
                    cash.getId(),
                    PaymentType.CASH,
                    cash.getAmount(),
                    cash.getReceivedAmount(),
                    cash.getChangeAmount(),
                    null
            );
            case CardPaymentEntity card -> new PaymentResponse(
                    card.getId(),
                    PaymentType.CARD,
                    card.getAmount(),
                    null,
                    null,
                    card.getApprovalNo()
            );
            default -> throw new IllegalArgumentException(
                String.format("Unsupported payment type: %s", payment.getClass().getSimpleName())
            );
        };
    }
    
    /**
     * 현금 결제 전용 생성 메서드
     */
    public static PaymentResponse cash(CashPaymentEntity cash) {
        return new PaymentResponse(
                cash.getId(),
                PaymentType.CASH,
                cash.getAmount(),
                cash.getReceivedAmount(),
                cash.getChangeAmount(),
                null
        );
    }
    
    /**
     * 카드 결제 전용 생성 메서드
     */
    public static PaymentResponse card(CardPaymentEntity card) {
        return new PaymentResponse(
                card.getId(),
                PaymentType.CARD,
                card.getAmount(),
                null,
                null,
                card.getApprovalNo()
        );
    }
    
    /**
     * List 변환 헬퍼 메서드
     */
    public static List<PaymentResponse> fromList(List<? extends PaymentEntity> payments) {
        if (payments == null || payments.isEmpty()) {
            return List.of();
        }
        return payments.stream()
                .map(PaymentResponse::from)
                .toList();
    }
    
    // =========================
    // Business Methods
    // =========================
    
    public boolean isCash() {
        return type == PaymentType.CASH;
    }
    
    public boolean isCard() {
        return type == PaymentType.CARD;
    }
    
    public boolean hasChange() {
        return changeAmount != null && changeAmount.compareTo(BigDecimal.ZERO) > 0;
    }
}