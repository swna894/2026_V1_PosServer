package com.swna.server.sale.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.swna.server.sale.entity.Sale;

public record SaleResponse(

        Long orderId,
        String receiptNo,
        String status,
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        List<SaleItemResponse> items,
        List<PaymentResponse> payments

) {

    // =========================
    // Factory (핵심)
    // =========================
    public static SaleResponse of(Sale order) {

        return new SaleResponse(
                order.getId(),
                order.getReceiptNo(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getItems().stream()
                        .map(SaleItemResponse::of)
                        .toList(),
                order.getPayments().stream()
                        .map(PaymentResponse::of)
                        .toList()
        );
    }
}
