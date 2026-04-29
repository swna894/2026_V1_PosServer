package com.swna.server.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_stocks")
public class ProductStock {

    private static final int DEFAULT_MIN_ORDER_QTY = 12; // 🔥 기본값

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", unique = true)
    private Product product;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int quantity = 0;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int minStock = 0;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 100")
    private int maxStock = 100;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 12")
    private int minOrderQuantity = 12; // optional (0이면 미설정)

    // =========================
    // Business Logic
    // =========================

    public boolean isLowStock() {
        return quantity <= minStock;
    }

    /**
     * 🔥 실제 사용되는 최소 주문 수량 (fallback 적용)
     */
    public int getEffectiveMinOrderQuantity() {
        return (minOrderQuantity <= 0) ? DEFAULT_MIN_ORDER_QTY : minOrderQuantity;
    }

    /**
     * 🔥 발주 수량 계산
     */
    public int calculateReorderQuantity() {

        if (maxStock <= 0) {
            throw new IllegalStateException("Max stock must be configured.");
        }

        int shortage = maxStock - quantity;

        if (shortage <= 0) {
            return 0;
        }

        int effectiveMinOrderQty = getEffectiveMinOrderQuantity();

        // 🔥 핵심
        return Math.max(shortage, effectiveMinOrderQty);
    }

    // =========================
    // Factory
    // =========================

    public static ProductStock create(Product product,
                                      int initialQty,
                                      int minStock,
                                      int maxStock,
                                      int minOrderQuantity) {

        if (product == null) {
            throw new IllegalArgumentException("Product must not be null.");
        }

        if (initialQty < 0) {
            throw new IllegalArgumentException("Initial quantity cannot be negative.");
        }

        ProductStock stock = new ProductStock();
        stock.product = product;
        stock.quantity = initialQty;
        stock.minStock = minStock;
        stock.maxStock = maxStock;
        stock.minOrderQuantity = minOrderQuantity; // 0 허용

        return stock;
    }

    // =========================
    // Stock Operations
    // =========================

    public void increase(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Increase amount must be greater than zero.");
        }
        this.quantity += amount;
    }

    public void decrease(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Decrease amount must be greater than zero.");
        }

        if (this.quantity < amount) {
            throw new IllegalStateException(
                String.format("Insufficient stock. Current: %d, Requested: %d", quantity, amount)
            );
        }

        this.quantity -= amount;
    }
}