package com.swna.server.order.domain;

import java.math.BigDecimal;

import com.swna.server.product.entity.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id     
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    // =========================
    // Snapshot (핵심)
    // =========================
    private String productBarcode;
    private String productName;
    private BigDecimal price;
    
    private int quantity;

    // =========================
    // Factory
    // =========================
    public static OrderItem of(Product product, int quantity) {

        OrderItem item = new OrderItem();

        // relation (optional)
        item.product = product;

        // snapshot (핵심)
        item.productBarcode = product.getBarcode();
        item.productName = product.getName();
        item.price = product.getPrice();

        item.quantity = quantity;

        return item;
    }
    // =========================
    // Business
    // =========================

    public BigDecimal getSubTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public void assignOrder(Order order) {
        this.order = order;
    }
}
