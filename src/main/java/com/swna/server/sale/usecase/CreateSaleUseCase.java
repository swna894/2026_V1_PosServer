package com.swna.server.sale.usecase;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.sale.dto.request_old.DiscountRequest;
import com.swna.server.sale.dto.request_old.SaleRequest;
import com.swna.server.sale.entity.Discount;
import com.swna.server.sale.entity.Sale;
import com.swna.server.sale.entity.SaleItem;
import com.swna.server.sale.mapper.SaleItemMapper;
import com.swna.server.sale.repository.SaleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateSaleUseCase {

    private final SaleItemMapper saleItemMapper;
    private final SaleRepository saleRepository;

    // =========================
    // Order 생성
    // =========================
    @Transactional
    public Long execute(SaleRequest request) {

        // 1. OrderItem 생성 (Product 조회 기반)
        List<SaleItem> items = request.items().stream()
                .map(saleItemMapper::toEntity)
                .toList();

        // 2. Discount 생성
        List<Discount> discounts = request.discounts().stream()
                .map(DiscountRequest::toDomain)
                .toList();

        // 3. Order 생성 (Domain 책임)
        Sale sale = Sale.create(items, discounts);

        // 4. 저장
        saleRepository.save(sale);

        return sale.getId();
    }

}