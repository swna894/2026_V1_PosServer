package com.swna.server.sale.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record SaleRequest(

        @NotEmpty(message = "주문 상품은 필수입니다")
        @Valid
        List<SaleItemRequest> items,

        @Valid
        List<DiscountRequest> discounts,

        @NotEmpty(message = "결제 정보는 필수입니다")
        @Valid
        List<PaymentRequest> payments

) {}