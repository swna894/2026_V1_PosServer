package com.swna.server.product.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "discounts")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 다대일 (여러 할인 가능)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Column(nullable = false)
    private boolean active = true;

    public enum DiscountType {
        RATE,   // %
        AMOUNT  // 정액
    }

    // =========================
    // 비즈니스 로직
    // =========================

    public boolean isActive(LocalDateTime now) {
        return active &&
                (startAt == null || now.isAfter(startAt)) &&
                (endAt == null || now.isBefore(endAt));
    }

    public BigDecimal apply(BigDecimal price) {
        if (type == DiscountType.RATE) {
            return price.subtract(price.multiply(value));
        } else {
            return price.subtract(value);
        }
    }
}