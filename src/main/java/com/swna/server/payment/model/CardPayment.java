package com.swna.server.payment.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CardPayment implements PaymentMethod {

    private final BigDecimal amount;
    private final String approvalNo;

    private CardPayment(BigDecimal amount, String approvalNo) {
        this.amount = amount;
        this.approvalNo = approvalNo;
    }

    public static CardPayment of(BigDecimal amount, String approvalNo) {
        return new CardPayment(amount, approvalNo);
    }

    // =========================
    // Business Logic
    // =========================

    public boolean isApproved() {
        return approvalNo != null && !approvalNo.isBlank();
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }
}
