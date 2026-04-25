package com.swna.server.order.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.swna.server.common.entity.BaseEntity;
import com.swna.server.payment.entity.PaymentEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal totalAmount = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(unique = true)
    private String receiptNo;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentEntity> payments = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Discount> discounts = new ArrayList<>();

    // =========================
    // Factory
    // =========================
    public static Order create(List<OrderItem> items, List<Discount> discounts) {
        Order order = new Order();

        items.forEach(order::addItem);
        discounts.forEach(order::addDiscount);

        order.recalculateAmounts();
        order.receiptNo = generateReceiptNo();
        order.status = OrderStatus.CREATED;

        return order;
    }

    // =========================
    // Business Methods
    // =========================

    public void addItem(OrderItem item) {
        items.add(item);
        item.assignOrder(this);
    }

    public void addDiscount(Discount discount) {
        discounts.add(discount);
        discount.assignOrder(this);
    }

    public void addPayment(PaymentEntity payment) {
        payments.add(payment);
        payment.assignOrder(this);
    }

    public void markPaid() {
        validatePayment();
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status == OrderStatus.PAID) {
            throw new IllegalStateException("이미 결제된 주문은 취소 불가");
        }
        this.status = OrderStatus.CANCELLED;
    }

    // =========================
    // Validation
    // =========================

    public void validatePayment() {
        BigDecimal totalPaid = payments.stream()
                .map(PaymentEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(totalAmount) != 0) {
            throw new IllegalStateException("결제 금액 불일치");
        }
    }

    // =========================
    // Calculation
    // =========================

    private void recalculateAmounts() {
        BigDecimal itemTotal = calculateItemTotal();
        BigDecimal discountTotal = calculateDiscountTotal(itemTotal);

        this.discountAmount = discountTotal;
        this.totalAmount = itemTotal.subtract(discountTotal);
    }

    private BigDecimal calculateItemTotal() {
        return items.stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDiscountTotal(BigDecimal baseAmount) {
        return discounts.stream()
                .map(d -> d.calculate(baseAmount))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // =========================
    // Utility
    // =========================

    private static String generateReceiptNo() {
        return "R-" + System.currentTimeMillis();
    }
}