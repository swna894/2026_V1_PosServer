package com.swna.server.sale.dto.request_old;

import java.math.BigDecimal;

import com.swna.server.payment.model.PaymentType;

/**
 * 결제 요청 DTO (Immutable Record)
 * @param type "CARD" 또는 "CASH"[cite: 4]
 * @param amount 물건 가격 또는 결제 대상 금액[cite: 1, 2]
 * @param receivedAmount (현금 시) 고객에게 받은 돈[cite: 2]
 * @param cashOutAmount (카드 시) 캐시아웃 요청 금액
 * @param approvalNo 카드 승인 번호[cite: 1]
 */
public record PaymentRequest(
    PaymentType type,
    BigDecimal amount,
    BigDecimal receivedAmount,
    BigDecimal cashOutAmount,
    String approvalNo
) {
    // 필요한 경우 콤팩트 생성자를 통해 기본값 설정이 가능합니다.
    public PaymentRequest {
        if (cashOutAmount == null) cashOutAmount = BigDecimal.ZERO;
    }
}
