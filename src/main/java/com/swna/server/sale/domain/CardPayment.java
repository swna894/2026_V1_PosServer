package com.swna.server.sale.domain;

import java.math.BigDecimal;

public record CardPayment(
    BigDecimal amount,
    String approvalNo
) implements PaymentMethod {
    
    public static CardPayment of(BigDecimal amount, String approvalNo) {
        if (amount == null || approvalNo == null) {
            throw new IllegalArgumentException("Amount and approvalNo cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (approvalNo.isBlank()) {
            throw new IllegalArgumentException("Approval number cannot be blank");
        }
        return new CardPayment(amount, approvalNo);
    }
    
    @Override
    public BigDecimal getAmount() {
        return amount;
    }
    
    @Override
    public String getType() {
        return "CARD";
    }
    
    @Override
    public boolean validate() {
        return amount != null 
            && amount.compareTo(BigDecimal.ZERO) > 0
            && approvalNo != null 
            && !approvalNo.isBlank();
    }
}