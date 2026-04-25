package com.swna.server.order.domain;

import java.math.BigDecimal;

import com.swna.server.product.entity.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private int quantity;

    private BigDecimal price;
    
    // =========================
    // Factory
    // =========================

    public static OrderItem of(Product product, int quantity) {

        OrderItem item = new OrderItem();

        item.product = product;
        item.quantity = quantity;
        item.price = product.getPrice();

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
