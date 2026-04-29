package com.swna.server.sale.mapper;

import org.springframework.stereotype.Component;

import com.swna.server.payment.entity.CardPaymentEntity;
import com.swna.server.payment.entity.CashPaymentEntity;
import com.swna.server.payment.entity.PaymentEntity;
import com.swna.server.payment.model.CardPayment;
import com.swna.server.payment.model.CashPayment;
import com.swna.server.payment.model.PaymentMethod;

@Component
public class PaymentMapper {

    public PaymentEntity toEntity(PaymentMethod payment) {

        if (payment instanceof CashPayment cash) {
            return CashPaymentEntity.of(
                    cash.getAmount(),
                    cash.getReceivedAmount()
            );
        }

        if (payment instanceof CardPayment card) {
            return CardPaymentEntity.of(
                    card.getAmount(),
                    card.getApprovalNo()
            );
        }

        throw new IllegalArgumentException("지원하지 않는 결제 타입");
    }

    public PaymentMethod toDomain(PaymentEntity entity) {

        if (entity instanceof CashPaymentEntity cash) {

            return CashPayment.of(
                    cash.getAmount(),
                    cash.getReceivedAmount()
            );
        }

        if (entity instanceof CardPaymentEntity card) {

            return CardPayment.of(
                    card.getAmount(),
                    card.getApprovalNo()
            );
        }

        throw new IllegalArgumentException("지원하지 않는 결제 타입");
    }
}
