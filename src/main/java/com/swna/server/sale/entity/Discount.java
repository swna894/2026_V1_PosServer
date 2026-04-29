package com.swna.server.sale.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "sale_discount")
public class Discount {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Sale sale;

    private String name;

    @Enumerated(EnumType.STRING)
    private DiscountType type;

    private BigDecimal value;

    public static Discount amount(String name, BigDecimal amount) {
        Discount d = new Discount();
        d.name = name;
        d.type = DiscountType.AMOUNT;
        d.value = amount;
        return d;
    }

    public static Discount percent(String name, BigDecimal percent) {
        Discount d = new Discount();
        d.name = name;
        d.type = DiscountType.PERCENT;
        d.value = percent;
        return d;
    }

    public BigDecimal calculate(BigDecimal baseAmount) {
        return switch (type) {
            case AMOUNT -> value;
            case PERCENT -> baseAmount
                    .multiply(value)
                    .divide(BigDecimal.valueOf(100));
        };
    }

    public void assignOrder(Sale sale) {
        this.sale = sale;
    }
}