package com.swna.server.order.usecase;


import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.discount.Discount;
import com.swna.server.order.domain.Order;
import com.swna.server.order.domain.OrderItem;
import com.swna.server.order.dto.CreateOrderRequest;
import com.swna.server.order.repository.OrderRepository;
import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Long execute(CreateOrderRequest request) {

        // 1. OrderItem 생성
        List<OrderItem> items = request.items().stream()
        .map(i -> {
            Product product = productRepository.findById(i.productId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

            return OrderItem.of(product, i.quantity());
        })
        .toList();

        // 2. Discount 생성
        List<Discount> discounts = request.discounts().stream()
                .map(d -> d.toDomain())
                .toList();

        // 3. Order 생성 (Domain 책임)
        Order order = Order.create(items, discounts);

        // 4. 저장
        orderRepository.save(order);

        return order.getId();
    }
}
