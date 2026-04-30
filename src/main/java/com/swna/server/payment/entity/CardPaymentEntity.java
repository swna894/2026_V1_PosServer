package com.swna.server.payment.entity;

import jakarta.persistence.Column;
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

    // 추가된 필드: 고객에게 지급한 현금(캐시아웃) 내역을 별도로 저장합니다.
    @Column(precision = 19, scale = 2)
    private BigDecimal cashOutAmount = BigDecimal.ZERO;

    // =========================
    // Factory helper (개선됨)
    // =========================

    /**
     * @param totalAmount   카드사에 승인 요청한 총 금액 (제품가 + 캐시아웃)
     * @param approvalNo    카드 승인 번호
     * @param cashOutAmount 실제 현금으로 지급된 금액 (캐시아웃 분)
     */
    public static CardPaymentEntity of(BigDecimal totalAmount, BigDecimal cashOutAmount, String approvalNo) {

        CardPaymentEntity entity = new CardPaymentEntity();

        // 부모 엔티티(PaymentEntity)의 amount 필드에는 승인 총액을 저장합니다.[cite: 7]
        entity.setAmount(totalAmount); 
        
        entity.approvalNo = approvalNo; //
        
        // 캐시아웃 금액을 별도로 기록하여 사후 정산 시 매출과 구분할 수 있게 합니다.[cite: 5]
        entity.cashOutAmount = (cashOutAmount != null) ? cashOutAmount : BigDecimal.ZERO;

        return entity;
    }
}
