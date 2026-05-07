package com.swna.server.sale.mapper;

import org.springframework.stereotype.Component;

import com.swna.server.sale.domain.CardPayment;
import com.swna.server.sale.domain.CashPayment;
import com.swna.server.sale.domain.PaymentMethod;
import com.swna.server.sale.dto.response.PaymentResponse;
import com.swna.server.sale.entity.CardPaymentEntity;
import com.swna.server.sale.entity.CashPaymentEntity;
import com.swna.server.sale.entity.PaymentEntity;
import com.swna.server.sale.entity.PaymentType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentMapper {

    /**
     * Domain → Entity 변환 (Record Pattern 적용)
     */
    public PaymentEntity toEntity(PaymentMethod payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }

        // ✅ Record Pattern: CashPayment(amount, receivedAmount) 형태로 직접 분해
        return switch (payment) {
            case CashPayment(var amount, var receivedAmount) -> 
                CashPaymentEntity.of(amount, receivedAmount);
            
            case CardPayment(var amount, var approvalNo) -> 
                CardPaymentEntity.of(amount, approvalNo);
            
            default -> throw new IllegalArgumentException(
                String.format("Unsupported payment type: %s", payment.getClass().getSimpleName())
            );
        };
    }

    /**
     * Entity → Domain 변환
     */
    public PaymentMethod toDomain(PaymentEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Payment entity cannot be null");
        }

        return switch (entity) {
            case CashPaymentEntity cash -> CashPayment.of(
                    cash.getAmount(),
                    cash.getReceivedAmount()
            );
            case CardPaymentEntity card -> CardPayment.of(
                    card.getAmount(),
                    card.getApprovalNo()
            );
            default -> throw new IllegalArgumentException(
                String.format("Unsupported payment entity type: %s", entity.getClass().getSimpleName())
            );
        };
    }
    
    /**
     * Domain → Entity 변환 (null-safe 버전)
     */
    public PaymentEntity toEntityOrNull(PaymentMethod payment) {
        if (payment == null) {
            log.warn("PaymentMethod is null, returning null");
            return null;
        }
        return toEntity(payment);
    }
    
    /**
     * Entity → Domain 변환 (null-safe 버전)
     */
    public PaymentMethod toDomainOrNull(PaymentEntity entity) {
        if (entity == null) {
            log.warn("PaymentEntity is null, returning null");
            return null;
        }
        return toDomain(entity);
    }

        
    public PaymentResponse toResponse(PaymentEntity payment) {
        if (payment instanceof CashPaymentEntity cash) {
            return PaymentResponse.builder()
                .type(PaymentType.CASH)
                .amount(cash.getAmount())
                .receivedAmount(cash.getReceivedAmount())
                .changeAmount(cash.getChangeAmount())
                .build();
        } else if (payment instanceof CardPaymentEntity card) {
            return PaymentResponse.builder()
                .type(PaymentType.CARD)
                .amount(card.getAmount())
                .cardNumber(card.getApprovalNo())
                .cardNumber(card.getCardNumber())
                .build();
        }
        throw new IllegalArgumentException("Unknown payment type");
    }
}