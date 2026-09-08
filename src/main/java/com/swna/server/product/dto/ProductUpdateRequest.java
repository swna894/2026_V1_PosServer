package com.swna.server.product.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductUpdateRequest(
        @NotNull(message = "수정할 상품 ID는 필수입니다.")
        Long id,

        String description,
        BigDecimal price,
        BigDecimal cost,
        BigDecimal priceOld,
        BigDecimal costOld,
        String category,
        @Min(0) Integer quantity,
        @Min(0) Integer minStock,
        @Min(0) Integer maxStock,
        @Min(0) Integer minOrderQuantity
) {}