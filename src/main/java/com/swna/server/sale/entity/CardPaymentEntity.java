package com.swna.server.sale.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CARD")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardPaymentEntity extends PaymentEntity {

    @Column(unique = true)
    @NotNull(groups = CardPaymentEntity.class) // 카드 결제일 때만 필수
    private String approvalNo;

    private String cardNumber;

    private String cardIssuer;

    public CardPaymentEntity(BigDecimal amount, String approvalNo) {
        super(amount);
        this.approvalNo = approvalNo;
    }

    public CardPaymentEntity(BigDecimal amount, String approvalNo, String cardNumber, String cardIssuer) {
        super(amount);
        this.approvalNo = approvalNo;
        this.cardNumber = maskCardNumber(cardNumber);
        this.cardIssuer = cardIssuer;
    }

    public static CardPaymentEntity of(BigDecimal amount, String approvalNo) {
        return new CardPaymentEntity(amount, approvalNo);
    }

    public static CardPaymentEntity of(BigDecimal amount, String approvalNo, String cardNumber, String cardIssuer) {
        return new CardPaymentEntity(amount, approvalNo, cardNumber, cardIssuer);
    }

    @Override
    public String getPaymentMethodName() {
        return PaymentType.CARD.getValue();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}