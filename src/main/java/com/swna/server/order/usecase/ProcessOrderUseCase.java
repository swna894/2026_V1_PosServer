package com.swna.server.order.usecase;


import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.order.domain.Discount;
import com.swna.server.order.domain.Order;
import com.swna.server.order.domain.OrderItem;
import com.swna.server.order.dto.request.DiscountRequest;
import com.swna.server.order.dto.request.OrderItemRequest;
import com.swna.server.order.dto.request.OrderRequest;
import com.swna.server.order.dto.response.OrderResponse;
import com.swna.server.order.event.OrderPaidEvent;
import com.swna.server.order.factory.PaymentFactory;
import com.swna.server.order.mapper.PaymentMapper;
import com.swna.server.order.repository.OrderRepository;
import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcessOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentFactory paymentFactory;
    private final PaymentMapper paymentMapper;
    private final ApplicationEventPublisher eventPublisher;

    // =========================
    // 주문 + 결제 통합 처리
    // =========================
    @Transactional
    public OrderResponse execute(OrderRequest request) {

        Order order = process(request);

        orderRepository.save(order);

        eventPublisher.publishEvent(new OrderPaidEvent(order.getId()));

        return OrderResponse.of(order);
    }

    private Order process(OrderRequest request) {

        List<OrderItem> items = request.items().stream()
                .map(this::toOrderItem)
                .toList();

        List<Discount> discounts = request.discounts().stream()
                .map(DiscountRequest::toDomain)
                .toList();

        Order order = Order.create(items, discounts);

        request.payments().stream()
                .map(paymentFactory::create)
                .map(paymentMapper::toEntity)
                .forEach(order::addPayment);

        order.validatePayment();
        order.markPaid();

        return order;
}
    // =========================
    // mapping
    // =========================
    private OrderItem toOrderItem(OrderItemRequest request) {

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        return OrderItem.of(product, request.quantity());
    }
}
