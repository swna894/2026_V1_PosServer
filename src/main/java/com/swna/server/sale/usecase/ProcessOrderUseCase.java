package com.swna.server.sale.usecase;


import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.sale.dto.request.DiscountRequest;
import com.swna.server.sale.dto.request.SaleItemRequest;
import com.swna.server.sale.dto.request.SaleRequest;
import com.swna.server.sale.dto.response.SaleResponse;
import com.swna.server.sale.entity.Discount;
import com.swna.server.sale.entity.Sale;
import com.swna.server.sale.entity.SaleItem;
import com.swna.server.sale.event.OrderPaidEvent;
import com.swna.server.sale.factory.PaymentFactory;
import com.swna.server.sale.mapper.PaymentMapper;
import com.swna.server.sale.repository.OrderRepository;

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
    public SaleResponse execute(SaleRequest request) {

        Sale order = process(request);

        orderRepository.save(order);

        eventPublisher.publishEvent(new OrderPaidEvent(order.getId()));

        return SaleResponse.of(order);
    }

    private Sale process(SaleRequest request) {

        List<SaleItem> items = request.items().stream()
                .map(this::toOrderItem)
                .toList();

        List<Discount> discounts = request.discounts().stream()
                .map(DiscountRequest::toDomain)
                .toList();

        Sale order = Sale.create(items, discounts);

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
    private SaleItem toOrderItem(SaleItemRequest request) {

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        return SaleItem.of(product, request.quantity());
    }
}
