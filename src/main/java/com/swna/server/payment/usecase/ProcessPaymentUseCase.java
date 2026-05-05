package com.swna.server.payment.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.payment.entity.PaymentEntity;
import com.swna.server.payment.model.PaymentMethod;
import com.swna.server.payment.repository.PaymentRepository;
import com.swna.server.sale.dto.request_old.PaymentRequest;
import com.swna.server.sale.entity.Sale;
import com.swna.server.sale.event.SalePaidEvent;
import com.swna.server.sale.factory.PaymentFactory;
import com.swna.server.sale.mapper.PaymentMapper;
import com.swna.server.sale.repository.SaleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcessPaymentUseCase {

    private final SaleRepository saleRepository;
    private final PaymentFactory paymentFactory;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void execute(Long saleId, PaymentRequest request) {

        // 1. 주문 조회
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));

        // 2. Domain 생성 (비즈니스 객체)
        PaymentMethod payment = paymentFactory.create(request);

        // 3. Entity 변환
        PaymentEntity entity = paymentMapper.toEntity(payment);

        // 4. DB 저장
        paymentRepository.save(entity);

        // 5. sale 연결
        sale.addPayment(entity);

        // 6. 결제 검증 + 상태 변경
        sale.markPaid();

        // 7. 이벤트 발행 (후처리)
        eventPublisher.publishEvent(new SalePaidEvent(sale.getId()));
    }
}
