package com.swna.server.sale.dto.request;

import java.math.BigDecimal;

import com.swna.server.sale.entity.PaymentType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PaymentRequest(

    @NotNull(message = "Payment type is required. (e.g., CASH, CARD)")
    PaymentType type,

    @NotNull(message = "Payment amount is required.")
    @PositiveOrZero(message = "Amount must be zero or positive.")
    BigDecimal amount, // Actual portion of the total amount being paid

    @PositiveOrZero(message = "Received amount must be zero or positive.")
    BigDecimal receivedAmount, // Total cash handed over by the customer

    @PositiveOrZero(message = "Cash-out amount must be zero or positive.")
    BigDecimal cashOutAmount, // Extra cash requested during card transaction

    String approvalNo // Card authorization number

) {
        // =========================
    // Business Methods
    // =========================
    
    public boolean isCash() {
        return type == PaymentType.CASH;
    }
    
    public boolean isCard() {
        return type == PaymentType.CARD;
    }
    
    public boolean hasReceivedAmount() {
        return receivedAmount != null;
    }
    
    public boolean hasApprovalNo() {
        return approvalNo != null && !approvalNo.isBlank();
    }
    
    public BigDecimal getChange() {
        if (isCash() && receivedAmount != null) {
            return receivedAmount.subtract(amount);
        }
        return BigDecimal.ZERO;
    }
}
