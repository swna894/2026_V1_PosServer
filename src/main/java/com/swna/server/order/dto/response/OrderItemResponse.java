package com.swna.server.order.dto.response;

import com.swna.server.order.domain.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(

        Long productId,
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
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubTotal()
        );
    }
}