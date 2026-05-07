package com.swna.server.sale.domain;

import java.math.BigDecimal;

public record CashPayment(
    BigDecimal amount,
    BigDecimal receivedAmount
) implements PaymentMethod {
    
    public static CashPayment of(BigDecimal amount, BigDecimal receivedAmount) {
        if (amount == null || receivedAmount == null) {
            throw new IllegalArgumentException("Amount and receivedAmount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (receivedAmount.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Received amount cannot be less than amount");
        }
        return new CashPayment(amount, receivedAmount);
    }
    
    @Override
    public BigDecimal getAmount() {
        return amount;
    }
    
    @Override
    public String getType() {
        return "CASH";
    }
    
    @Override
    public boolean validate() {
        return amount != null 
            && receivedAmount != null 
            && amount.compareTo(BigDecimal.ZERO) > 0
            && receivedAmount.compareTo(amount) >= 0;
    }
    
    public BigDecimal getChange() {
        return receivedAmount.subtract(amount);
    }
}