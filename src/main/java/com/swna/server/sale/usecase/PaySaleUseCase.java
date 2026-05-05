package com.swna.server.sale.usecase;

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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaySaleUseCase {

    private final SaleRepository orderRepository;
    private final PaymentFactory paymentFactory;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void execute(@NonNull Long orderId, PaymentRequest request) {

        // 1. 주문 조회
        Sale order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));

        // 2. Domain Payment 생성
        PaymentMethod payment = paymentFactory.create(request);

        // 3. Entity 변환
        PaymentEntity entity = paymentMapper.toEntity(payment);

        // 4. Order 연결
        order.addPayment(entity);

        // 5. 저장
        paymentRepository.save(entity);

        // 6. 검증 + 상태 변경
        order.markPaid();

        // 7. 이벤트 발행
        eventPublisher.publishEvent(new SalePaidEvent(order.getId()));
    }
}
