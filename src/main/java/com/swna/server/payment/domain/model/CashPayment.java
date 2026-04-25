package com.swna.server.payment.domain.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CashPayment implements PaymentMethod {

    private final BigDecimal amount;
    private final BigDecimal receivedAmount;

    private CashPayment(BigDecimal amount, BigDecimal receivedAmount) {
        this.amount = amount;
        this.receivedAmount = receivedAmount;
    }

    public static CashPayment of(BigDecimal amount, BigDecimal receivedAmount) {
        return new CashPayment(amount, receivedAmount);
    }

    // =========================
    // Business Logic
    // =========================

    public BigDecimal getChange() {
        return receivedAmount.subtract(amount);
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }
}