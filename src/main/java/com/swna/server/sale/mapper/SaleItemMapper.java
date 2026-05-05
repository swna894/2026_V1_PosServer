package com.swna.server.sale.mapper;

import org.springframework.stereotype.Component;

import com.swna.server.product.entity.Product;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.sale.dto.request_old.SaleItemRequest;
import com.swna.server.sale.entity.SaleItem;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SaleItemMapper {
    private final ProductRepository productRepository;

    public SaleItem toEntity(SaleItemRequest request) {
        Product product = productRepository.findByBarcode(request.barcode()) // 혹은 productBarcode()
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));
        
        return SaleItem.of(product, request.quantity());
    }
}
