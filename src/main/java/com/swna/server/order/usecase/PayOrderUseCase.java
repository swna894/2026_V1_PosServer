package com.swna.server.order.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.order.domain.Order;
import com.swna.server.order.dto.PaymentRequest;
import com.swna.server.order.event.OrderPaidEvent;
import com.swna.server.order.repository.OrderRepository;
import com.swna.server.payment.domain.entity.PaymentEntity;
import com.swna.server.payment.domain.model.PaymentMethod;
import com.swna.server.payment.factory.PaymentFactory;
import com.swna.server.payment.mapper.PaymentMapper;
import com.swna.server.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PayOrderUseCase {

    private final OrderRepository orderRepository;
    private final PaymentFactory paymentFactory;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void execute(Long orderId, PaymentRequest request) {

        // 1. 주문 조회
        Order order = orderRepository.findById(orderId)
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
        eventPublisher.publishEvent(new OrderPaidEvent(order.getId()));
    }
}
