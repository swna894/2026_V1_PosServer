package com.swna.server.sale.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CASH")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashPaymentEntity extends PaymentEntity {

    @Column(columnDefinition = "DECIMAL(19,2) DEFAULT 0.00")
    private BigDecimal receivedAmount = BigDecimal.ZERO;
    @Column(columnDefinition = "DECIMAL(19,2) DEFAULT 0.00")
    private BigDecimal changeAmount = BigDecimal.ZERO;

    public CashPaymentEntity(BigDecimal amount, BigDecimal receivedAmount) {
        super(amount);
        this.receivedAmount = receivedAmount;
        this.changeAmount = receivedAmount.subtract(amount);
    }

    public static CashPaymentEntity of(BigDecimal amount, BigDecimal receivedAmount) {
        return new CashPaymentEntity(amount, receivedAmount);
    }

    @Override
    public String getPaymentMethodName() {
        return "CASH";
    }

    public boolean isExactAmount() {
        return changeAmount != null && changeAmount.compareTo(BigDecimal.ZERO) == 0;
    }
}