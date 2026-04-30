package com.swna.server.payment.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.swna.server.payment.entity.CardPaymentEntity;
import com.swna.server.payment.entity.CashPaymentEntity;
import com.swna.server.payment.entity.PaymentEntity;
import com.swna.server.payment.model.CardPayment;
import com.swna.server.payment.model.PaymentType;
import com.swna.server.payment.repository.PaymentRepository;
import com.swna.server.sale.dto.request.PaymentRequest;
import com.swna.server.sale.entity.Sale;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PosPaymentService {

    private final PaymentRepository paymentRepository; //

    public void processSalePayments(Sale sale, List<PaymentRequest> requests) {
        for (PaymentRequest req : requests) {
            PaymentEntity paymentEntity;

            if (PaymentType.CARD.equals(req.type())) {
                // 1. 도메인 모델 생성 (record의 메서드 접근 방식 준수)
                CardPayment cardModel = CardPayment.of(
                    req.amount(), 
                    req.cashOutAmount(), // record 내부에서 null 처리가 되어 있으므로 바로 호출 가능
                    req.approvalNo()
                );

                // 2. 카드 엔티티 생성 (총액 및 캐시아웃 상세 내역)
                paymentEntity = CardPaymentEntity.of(
                    cardModel.getAmount(), // 물건값 + 캐시아웃 총합
                    cardModel.getCashOutAmount(),
                    cardModel.getApprovalNo()
                );

            } else if (PaymentType.CASH.equals(req.type())) { // 괄호 오류 해결 지점
                // 3. 현금 엔티티 생성[cite: 2, 6]
                paymentEntity = CashPaymentEntity.of(
                    req.amount(), 
                    req.receivedAmount()
                );
            } else {
                throw new IllegalArgumentException("지원하지 않는 결제 타입입니다.");
            }

            // 4. 공통 연관관계 설정 및 저장[cite: 7, 8]
            paymentEntity.assignOrder(sale);
            paymentRepository.save(paymentEntity);
        }
    }
}