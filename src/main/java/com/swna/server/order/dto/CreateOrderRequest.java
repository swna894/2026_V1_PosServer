package com.swna.server.order.dto;

import java.util.List;

public record CreateOrderRequest(
        List<OrderItemRequest> items,
        List<DiscountRequest> discounts
) {
}