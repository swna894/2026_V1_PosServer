package com.swna.server.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.swna.server.product.entity.Product;
import com.swna.server.product.entity.ProductStock;

public record ProductResponse(
        String code,
        String barcode,
        String description,
        BigDecimal price,        // 판매가
        BigDecimal cost,         // 원가
        BigDecimal priceOld,
        BigDecimal costOld,
        int quantity,            // 현재 재고 수량
        int minStock,            // 적정/최소 재고 수량
        int minOrderQuantity,    // 최소 주문 수량
        LocalDateTime lastOrderedAt // 🔥 주문/수량 변경 일자
) {
    /**
     * Product와 ProductStock 엔티티를 모두 받아 DTO로 변환
     */
    public static ProductResponse from(Product product, ProductStock stock) {
        return new ProductResponse(
                product.getCode(),
                product.getBarcode(),
                product.getDescription(),
                product.getPrice(),
                product.getCost(),
                product.getPriceOld(),
                product.getCostOld(),
                stock != null ? stock.getQuantity() : 0,
                stock != null ? stock.getMinStock() : 0,
                stock != null ? stock.getEffectiveMinOrderQuantity() : 0,
                stock != null ? stock.getLastOrderedAt() : null
        );
    }

    /**
     * ProductStock 정보가 없고 개별 수량 값들만 전달받는 경우
     */
    public static ProductResponse from(
            Product product, 
            int quantity, 
            int minStock, 
            int minOrderQuantity,
            LocalDateTime lastOrderedAt
    ) {
        return new ProductResponse(
                product.getCode(),
                product.getBarcode(),
                product.getDescription(),
                product.getPrice(),
                product.getCost(),
                product.getPriceOld(),
                product.getCostOld(),
                quantity,
                minStock,
                minOrderQuantity,
                lastOrderedAt
        );
    }
}