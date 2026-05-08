package com.swna.server.sale.entity;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Discount {

    private DiscountType type;
    private BigDecimal value;

    private String reason;

    @Builder
    public Discount(DiscountType type, BigDecimal value, String reason) {
        this.type = type;
        this.value = value;
        this.reason = reason;
    }
}

