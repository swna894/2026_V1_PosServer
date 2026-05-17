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

    @NotNull(groups = CardPaymentEntity.class) // 카드 결제일 때만 필수
    @Column(columnDefinition = "VARCHAR(31) DEFAULT ''")
    private String approvalNo;
    
    @Column(columnDefinition = "DECIMAL(19,2) DEFAULT 0.00")
    private BigDecimal creditAmount;

    @Column(columnDefinition = "DECIMAL(19,2) DEFAULT 0.00")
    private BigDecimal cashAmount;

    @Column(columnDefinition = "VARCHAR(31) DEFAULT ''")
    private String cardNumber;

    @Column(columnDefinition = "VARCHAR(31) DEFAULT ''")
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

    // ✅ creditAmount와 cashAmount를 추가로 받아 채워주는 생성자
    public CardPaymentEntity(BigDecimal amount, BigDecimal creditAmount, BigDecimal cashAmount, String approvalNo, String cardNumber) {
        super(amount);
        this.creditAmount = creditAmount;
        this.cashAmount = cashAmount;
        this.approvalNo = approvalNo;
        this.cardNumber = maskCardNumber(cardNumber);
    }
    
    // ✅ creditAmount와 cashAmount를 추가로 받아 채워주는 생성자 추가
    public CardPaymentEntity(BigDecimal amount, BigDecimal creditAmount, BigDecimal cashAmount, String approvalNo, String cardNumber, String cardIssuer) {
        super(amount);
        this.creditAmount = creditAmount;
        this.cashAmount = cashAmount;
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

    // ✅ 컴파일 에러 해결을 위해 새로 추가한 5개짜리 정적 팩토리 메서드
    public static CardPaymentEntity of(BigDecimal amount, BigDecimal creditAmount, BigDecimal cashAmount, String approvalNo, String cardNumber) {
        return new CardPaymentEntity(amount, creditAmount, cashAmount, approvalNo, cardNumber);
    }
    @Override
    public String getPaymentMethodName() {
        return PaymentType.CARD.getValue();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        //return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
         return cardNumber;
    }
}