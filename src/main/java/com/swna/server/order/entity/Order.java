package com.swna.server.order.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.swna.server.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

    private BigDecimal amount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private Boolean sync;
    @Column(nullable = false, length = 32)
    private String invoice;
    
    @Column(name = "supplier_abbr", nullable = false, length = 8)
    private String supplierAbbr;
    private LocalDateTime inspected;

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

        this.amount = itemTotal;
    }

    private BigDecimal calculateItemTotal() {
        return items.stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}