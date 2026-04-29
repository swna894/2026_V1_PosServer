package com.swna.server.sale.dto.request;

import java.math.BigDecimal;

import com.swna.server.sale.entity.Discount;
import com.swna.server.sale.entity.DiscountType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record DiscountRequest(

        @NotNull(message = "할인 타입은 필수입니다")
        DiscountType type,

        @NotNull(message = "할인 값은 필수입니다")
        @DecimalMin(value = "0.0", inclusive = false, message = "할인 값은 0보다 커야 합니다")
        BigDecimal value

) {
    public Discount toDomain() {
        return switch (type) {
            case AMOUNT -> Discount.amount("DISCOUNT", value);
            case PERCENT -> Discount.percent("DISCOUNT", value);
            default -> throw new IllegalArgumentException("invalid discount type");
        };
    }
}
