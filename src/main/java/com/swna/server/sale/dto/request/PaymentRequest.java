package com.swna.server.sale.dto.request;

import java.math.BigDecimal;

import com.swna.server.payment.model.PaymentType;

public record PaymentRequest(
        PaymentType type,              // CASH / CARD
        BigDecimal amount,
        BigDecimal receivedAmount, // CASH만
        String approvalNo         // CARD만
) {}
