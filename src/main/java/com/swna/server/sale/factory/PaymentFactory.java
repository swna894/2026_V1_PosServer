package com.swna.server.sale.factory;

import org.springframework.stereotype.Component;

import com.swna.server.sale.domain.CardPayment;
import com.swna.server.sale.domain.CashPayment;
import com.swna.server.sale.domain.PaymentMethod;
import com.swna.server.sale.dto.request.PaymentRequest;

@Component
public class PaymentFactory {

    public PaymentMethod create(PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null");
        }

        return switch (request.type()) {
            case CASH -> CashPayment.of(request.amount(), request.receivedAmount());
            case CARD -> CardPayment.of(request.amount(), request.approvalNo());
            default -> throw new IllegalArgumentException(
                String.format("Unsupported payment type: %s", request.type())
            );
        };
    }
}