package com.swna.server.sale.dto.response;

import java.math.BigDecimal;

import com.swna.server.sale.entity.SaleItem;

public record SaleItemResponse(

        String productBarcode,
        String productName,
        int quantity,
        BigDecimal price,
        BigDecimal subTotal

) {

    // =========================
    // Factory
    // =========================
    public static SaleItemResponse of(SaleItem item) {

        return new SaleItemResponse(
                item.getProductBarcode(),
                item.getProductDescription(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubTotal()
        );
    }
}