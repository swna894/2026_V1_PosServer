package com.swna.server.payment.factory;

import org.springframework.stereotype.Component;

import com.swna.server.order.dto.PaymentRequest;
import com.swna.server.payment.domain.model.CardPayment;
import com.swna.server.payment.domain.model.CashPayment;
import com.swna.server.payment.domain.model.PaymentMethod;


@Component
public class PaymentFactory {

    public PaymentMethod create(PaymentRequest req) {

        return switch (req.type()) {

            case CASH -> CashPayment.of(
                    req.amount(),
                    req.receivedAmount()
            );

            case CARD -> CardPayment.of(
                    req.amount(),
                    req.approvalNo()
            );
        };
    }
}
