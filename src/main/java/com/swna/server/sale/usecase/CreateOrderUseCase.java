package com.swna.server.sale.usecase;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.sale.dto.request.DiscountRequest;
import com.swna.server.sale.dto.request.SaleItemRequest;
import com.swna.server.sale.dto.request.SaleRequest;
import com.swna.server.sale.entity.Discount;
import com.swna.server.sale.entity.Sale;
import com.swna.server.sale.entity.SaleItem;
import com.swna.server.sale.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    // =========================
    // Order 생성
    // =========================
    @Transactional
    public Long execute(SaleRequest request) {

        // 1. OrderItem 생성 (Product 조회 기반)
        List<SaleItem> items = request.items().stream()
                .map(this::toOrderItem)
                .toList();

        // 2. Discount 생성
        List<Discount> discounts = request.discounts().stream()
                .map(DiscountRequest::toDomain)
                .toList();

        // 3. Order 생성 (Domain 책임)
        Sale order = Sale.create(items, discounts);

        // 4. 저장
        orderRepository.save(order);

        return order.getId();
    }

    // =========================
    // Mapping
    // =========================

    private SaleItem toOrderItem(SaleItemRequest request) {

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        return SaleItem.of(product, request.quantity());
    }
}