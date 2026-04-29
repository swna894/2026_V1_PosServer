package com.swna.server.product.entity;

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
@Table(name = "stock_histories")
public class StockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 다대일 (History → Product)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantity; // +입고 / -출고

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockType type;

    private String reference; // 주문번호 등

    private LocalDateTime createdAt;

    public enum StockType {
        IN, OUT, RETURN
    }

    // =========================
    // 생성
    // =========================
    public static StockHistory of(Product product, int quantity, StockType type, String ref) {
        StockHistory h = new StockHistory();
        h.product = product;
        h.quantity = quantity;
        h.type = type;
        h.reference = ref;
        h.createdAt = LocalDateTime.now();
        return h;
    }
}
