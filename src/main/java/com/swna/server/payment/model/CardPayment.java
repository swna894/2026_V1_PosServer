package com.swna.server.payment.model;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class CardPayment implements PaymentMethod {

    private final BigDecimal productAmount; // 순수 물건 가격
    private final BigDecimal cashOutAmount; // 고객에게 지급할 현금(캐시아웃) 금액
    private final String approvalNo;        // 카드사 승인 번호

    // 생성자를 private으로 제한하고 정적 팩토리 메서드 사용
    private CardPayment(BigDecimal productAmount, BigDecimal cashOutAmount, String approvalNo) {
        this.productAmount = productAmount != null ? productAmount : BigDecimal.ZERO;
        this.cashOutAmount = cashOutAmount != null ? cashOutAmount : BigDecimal.ZERO;
        this.approvalNo = approvalNo;
    }

    /**
     * 카드 결제 객체 생성 (캐시아웃 포함 가능)
     * @param productAmount 순수 제품 금액
     * @param cashOutAmount 캐시아웃 금액 (없으면 0)
     * @param approvalNo 승인 번호
     */
    public static CardPayment of(BigDecimal productAmount, BigDecimal cashOutAmount, String approvalNo) {
        return new CardPayment(productAmount, cashOutAmount, approvalNo);
    }

    // =========================
    // Business Logic
    // =========================

    /**
     * 인터페이스 구현: 카드사에 승인 요청할 최종 총액을 반환합니다.
     * 총액 = 제품가 + 캐시아웃 금액[cite: 1, 3]
     */
    @Override
    public BigDecimal getAmount() {
        return productAmount.add(cashOutAmount);
    }

    /**
     * 승인 번호가 존재하는지 확인합니다.
     */
    public boolean isApproved() {
        return approvalNo != null && !approvalNo.isBlank();
    }

    /**
     * 해당 결제 건에 캐시아웃이 포함되어 있는지 확인합니다.
     */
    public boolean hasCashOut() {
        return cashOutAmount.compareTo(BigDecimal.ZERO) > 0;
    }
}
