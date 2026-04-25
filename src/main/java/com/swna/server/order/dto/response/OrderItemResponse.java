package com.swna.server.order.dto.response;

import com.swna.server.order.domain.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(

        String productBarcode,
        String productName,
        int quantity,
        BigDecimal price,
        BigDecimal subTotal

) {

    // =========================
    // Factory
    // =========================
    public static OrderItemResponse of(OrderItem item) {

        return new OrderItemResponse(
                item.getProductBarcode(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubTotal()
        );
    }
}