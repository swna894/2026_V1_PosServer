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
import com.swna.server.sale.repository.SaleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcessSaleUseCase {

    private final SaleRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentFactory paymentFactory;
    private final PaymentMapper paymentMapper;
    private final ApplicationEventPublisher eventPublisher;

    // =========================
    // 주문 + 결제 통합 처리
    // =========================
    @Transactional
    public SaleResponse execute(SaleRequest request) {

        Sale sale = process(request);

        orderRepository.save(sale);

        eventPublisher.publishEvent(new OrderPaidEvent(sale.getId()));

        return SaleResponse.of(sale);
    }

    private Sale process(SaleRequest request) {
        // A. 상품 내역 생성
        List<SaleItem> items = request.items().stream()
                .map(this::toSaleItem)
                .toList();

        // B. 할인 내역 생성 (DiscountRequest -> Discount 도메인)
        List<Discount> discounts = request.discounts().stream()
                .map(DiscountRequest::toDomain)
                .toList();

        // C. 주문 도메인 생성 (아이템 + 할인)
        Sale sale = Sale.create(items, discounts);

        // D. 결제 내역 생성 및 연결
        request.payments().stream()
                .map(paymentFactory::create) // DTO -> Domain (캐시아웃 등 계산 포함)
                .map(paymentMapper::toEntity) // Domain -> Entity
                .forEach(sale::addPayment); // 주문에 결제 정보 추가

        // E. 검증 및 상태 변경
        sale.validatePayment(); // 계산된 총액과 실제 결제액 일치 여부 확인
        sale.markPaid(); // 결제 완료 상태(PAID)로 변경

        return sale;
    }

    // =========================
    // mapping
    // =========================
    private SaleItem toSaleItem(SaleItemRequest request) {

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        return SaleItem.of(product, request.quantity());
    }
    
}
