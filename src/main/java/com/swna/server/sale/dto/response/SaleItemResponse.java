package com.swna.server.sale.dto.response;

import java.math.BigDecimal;

import com.swna.server.sale.entity.SaleItem;

public record SaleItemResponse(
        Long productId,
        String productBarcode,
        String productName,
        int quantity,
        BigDecimal priceAtSale,
        BigDecimal discountValue,
        String discountType,
        BigDecimal totalAmount,
        String comment
) {

    public static SaleItemResponse from(SaleItem item, String productName) {
        return new SaleItemResponse(
                item.getProductId(),
                item.getBarcode(),
                productName,
                item.getQuantity(),
                item.getPriceAtSale(),
                item.getDiscountValue(),
                item.getDiscountType() != null ? item.getDiscountType().name() : "FIXED",
                item.getTotalAmount(),
                item.getComment()
        );
    }
}