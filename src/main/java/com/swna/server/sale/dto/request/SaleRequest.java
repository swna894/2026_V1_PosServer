package com.swna.server.sale.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record SaleRequest(

        @NotEmpty(message = "Order items are required")
        @Valid
        List<SaleItemRequest> items,

        @Valid
        List<DiscountRequest> discounts,

        @NotEmpty(message = "Payment information is required")
        @Valid
        List<PaymentRequest> payments

) {}