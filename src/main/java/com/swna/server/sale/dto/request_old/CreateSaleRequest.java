package com.swna.server.sale.dto.request_old;

import java.util.List;

public record CreateSaleRequest(
        List<SaleItemRequest> items,
        List<DiscountRequest> discounts
) {
}