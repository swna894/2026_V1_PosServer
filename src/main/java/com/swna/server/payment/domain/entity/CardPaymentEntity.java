package com.swna.server.payment.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "card_payment")
public class CardPaymentEntity extends PaymentEntity {

    private String approvalNo;

    public void setApprovalNo(String approvalNo) {
        this.approvalNo = approvalNo;
    }

    // =========================
    // Factory helper
    // =========================

    public static CardPaymentEntity of(BigDecimal amount, String approvalNo) {

        CardPaymentEntity entity = new CardPaymentEntity();

        entity.setAmount(amount);
        entity.approvalNo = approvalNo;

        return entity;
    }
}
