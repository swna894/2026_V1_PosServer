package com.swna.server.payment.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "cash_payment")
public class CashPaymentEntity extends PaymentEntity {

    private BigDecimal receivedAmount;

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    // =========================
    // Factory helper
    // =========================

    public static CashPaymentEntity of(BigDecimal amount, BigDecimal receivedAmount) {

        CashPaymentEntity entity = new CashPaymentEntity();

        entity.setAmount(amount);
        entity.receivedAmount = receivedAmount;

        return entity;
    }
}