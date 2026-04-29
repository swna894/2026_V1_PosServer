package com.swna.server.sale.dto.request;

import java.util.List;

public record CreateOrderRequest(
        List<SaleItemRequest> items,
        List<DiscountRequest> discounts
) {
}