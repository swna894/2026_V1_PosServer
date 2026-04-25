package com.swna.server.order.dto;

import java.math.BigDecimal;

import com.swna.server.payment.domain.model.PaymentType;

public record PaymentRequest(
        PaymentType type,              // CASH / CARD
        BigDecimal amount,
        BigDecimal receivedAmount, // CASH만
        String approvalNo         // CARD만
) {}
