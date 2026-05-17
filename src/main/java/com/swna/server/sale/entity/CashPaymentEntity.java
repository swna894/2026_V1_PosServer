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
    private BigDecimal cashAmount;

    @Column(columnDefinition = "DECIMAL(19,2) DEFAULT 0.00")
    private BigDecimal changeAmount = BigDecimal.ZERO;

    public CashPaymentEntity(BigDecimal amount, BigDecimal receivedAmount) {
        super(amount);
        this.receivedAmount = receivedAmount;
        this.changeAmount = receivedAmount.subtract(amount);
    }

    private CashPaymentEntity(BigDecimal amount, BigDecimal receivedAmount, BigDecimal cashAmount) {
        super(amount);
        this.receivedAmount = receivedAmount;
        this.cashAmount = cashAmount; // ✅ 정밀하게 cashAmount를 할당
    }

    // ✅ 기존 2개짜리 호환용 생성자 및 팩토리
    public static CashPaymentEntity of(BigDecimal amount, BigDecimal receivedAmount) {
        return new CashPaymentEntity(amount, receivedAmount, amount); // cashAmount 기본값을 amount로 세팅
    }

    // ✅ 3개의 인자를 모두 받는 명확한 팩토리 메서드 추가
    public static CashPaymentEntity of(BigDecimal amount, BigDecimal receivedAmount, BigDecimal cashAmount) {
        return new CashPaymentEntity(amount, receivedAmount, cashAmount);
    }

    @Override
    public String getPaymentMethodName() {
        return "CASH";
    }

    public boolean isExactAmount() {
        return changeAmount != null && changeAmount.compareTo(BigDecimal.ZERO) == 0;
    }
}