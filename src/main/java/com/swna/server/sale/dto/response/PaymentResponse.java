package com.swna.server.sale.dto.response;

import java.math.BigDecimal;

import com.swna.server.payment.entity.CardPaymentEntity;
import com.swna.server.payment.entity.CashPaymentEntity;
import com.swna.server.payment.entity.PaymentEntity;

public record PaymentResponse(

        String type,
        BigDecimal amount,
        String detail

) {

    // =========================
    // Factory
    // =========================
    public static PaymentResponse of(PaymentEntity entity) {

        if (entity instanceof CashPaymentEntity cash) {

            return new PaymentResponse(
                    "CASH",
                    cash.getAmount(),
                    "received=" + cash.getAmount()
            );
        }

        if (entity instanceof CardPaymentEntity card) {

            return new PaymentResponse(
                    "CARD",
                    card.getAmount(),
                    "approvalNo=" + card.getApprovalNo()
            );
        }

        throw new IllegalArgumentException("지원하지 않는 결제 타입");
    }
}