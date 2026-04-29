package com.swna.server.sale.factory;

import org.springframework.stereotype.Component;

import com.swna.server.payment.model.CardPayment;
import com.swna.server.payment.model.CashPayment;
import com.swna.server.payment.model.PaymentMethod;
import com.swna.server.sale.dto.request.PaymentRequest;


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
