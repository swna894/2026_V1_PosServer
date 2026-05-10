package com.swna.server.sale.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CASHOUT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashoutPaymentEntity extends PaymentEntity {

    private BigDecimal receivedAmount;
    private BigDecimal cashoutAmount;
    
    // ✅ 카드 정보 필드 추가 (CardPaymentEntity와 동일하게)
    private String cardNumber;
    private String approvalNo;
    private String cardIssuer;

    // ✅ 모든 필드를 받는 생성자
    private CashoutPaymentEntity(
        BigDecimal amount,
        BigDecimal receivedAmount,
        BigDecimal cashoutAmount,
        String approvalNo,
        String cardNumber,
        String cardIssuer
    ) {
        super(amount);
        this.receivedAmount = receivedAmount;
        this.cashoutAmount = cashoutAmount;
        this.approvalNo = approvalNo;
        this.cardNumber = cardNumber;
        this.cardIssuer = cardIssuer;
    }

    // ✅ 카드 정보 포함한 of 메서드
    public static CashoutPaymentEntity of(
        BigDecimal amount,
        BigDecimal receivedAmount,
        BigDecimal cashoutAmount,
        String approvalNo,
        String cardNumber
    ) {
        return new CashoutPaymentEntity(amount, receivedAmount, cashoutAmount, approvalNo, cardNumber, null);
    }

    @Override
    public String getPaymentMethodName() {
        return "CASHOUT";
    }
}