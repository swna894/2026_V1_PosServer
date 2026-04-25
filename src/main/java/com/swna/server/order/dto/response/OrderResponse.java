package com.swna.server.order.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.swna.server.order.domain.Order;

public record OrderResponse(

        Long orderId,
        String receiptNo,
        String status,
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        List<OrderItemResponse> items,
        List<PaymentResponse> payments

) {

    // =========================
    // Factory (핵심)
    // =========================
    public static OrderResponse of(Order order) {

        return new OrderResponse(
                order.getId(),
                order.getReceiptNo(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getItems().stream()
                        .map(OrderItemResponse::of)
                        .toList(),
                order.getPayments().stream()
                        .map(PaymentResponse::of)
                        .toList()
        );
    }
}
