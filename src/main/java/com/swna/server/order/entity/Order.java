package com.swna.server.order.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.swna.server.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // =========================
    // Factory
    // =========================
    public static Order create(List<OrderItem> items) {
        Order order = new Order();
        items.forEach(order::addItem);
        order.recalculateAmounts();
        return order;
    }

    // =========================
    // Business Methods
    // =========================

    public void addItem(OrderItem item) {
        items.add(item);
        item.assignOrder(this);
    }



    // =========================
    // Calculation
    // =========================

    private void recalculateAmounts() {
        BigDecimal itemTotal = calculateItemTotal();

        this.totalAmount = itemTotal;
    }

    private BigDecimal calculateItemTotal() {
        return items.stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}