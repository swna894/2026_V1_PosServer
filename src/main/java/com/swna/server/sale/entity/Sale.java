package com.swna.server.sale.entity;

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
@Table(name = "sales")
public class Sale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalAmount = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(unique = true)
    private String receiptNo;

    @Enumerated(EnumType.STRING)
    private SaleStatus status = SaleStatus.CREATED;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentEntity> payments = new ArrayList<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Discount> discounts = new ArrayList<>();

    // =========================
    // Factory
    // =========================
    public static Sale create(List<SaleItem> items, List<Discount> discounts) {
        Sale order = new Sale();

        items.forEach(order::addItem);
        discounts.forEach(order::addDiscount);

        order.recalculateAmounts();
        order.receiptNo = generateReceiptNo();
        order.status = SaleStatus.CREATED;

        return order;
    }

    // =========================
    // Business Methods
    // =========================

    public void addItem(SaleItem item) {
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
        this.status = SaleStatus.PAID;
    }

    public void cancel() {
        if (status == SaleStatus.PAID) {
            throw new IllegalStateException("이미 결제된 주문은 취소 불가");
        }
        this.status = SaleStatus.CANCELLED;
    }

    // =========================
    // Validation
    // =========================

    public void validatePayment() {
        // 1. 서버가 계산한 최종 주문 금액 (물건값 - 할인)
        BigDecimal requiredAmount = this.totalAmount;

        // 2. 실제로 추가된 모든 결제 수단(현금, 카드 등)의 합계 계산
        BigDecimal totalPaid = payments.stream()
                .map(PaymentEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 두 금액이 일치하지 않으면 예외 발생 (트랜잭션 롤백 유도)[cite: 13, 17]
        if (requiredAmount.compareTo(totalPaid) != 0) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다. (기대 금액: " 
                + requiredAmount + ", 실제 결제액: " + totalPaid + ")");
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
                .map(SaleItem::getSubTotal)
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