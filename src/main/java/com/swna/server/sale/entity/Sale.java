package com.swna.server.sale.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.swna.server.common.entity.BaseEntity;
import com.swna.server.common.exception.ExceptionUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "sales")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String receiptNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status = SaleStatus.PENDING;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal finalAmount;

    private String memo;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentEntity> payments = new ArrayList<>();  
    // =========================
    // Static Factory Methods
    // =========================
    
    public static Sale create(List<SaleItem> items, List<Discount> discounts) {
        Sale sale = new Sale();
        sale.addItems(items);
        
        if (discounts != null && !discounts.isEmpty()) {
            sale.applyDiscounts(discounts);
        }
        
        sale.recalculateAmounts();
        return sale;
    }

    public static Sale create(List<SaleItem> items) {
        return create(items, List.of());
    }


    // =========================
    // Business Methods
    // =========================
    
    public void addItem(SaleItem item) {
        items.add(item);
        item.setSale(this);
        recalculateAmounts();
    }
    
    public void addItems(List<SaleItem> newItems) {
        newItems.forEach(this::addItem);
    }
    
    // ✅ PaymentEntity를 받도록 변경
    public void addPayment(PaymentEntity payment) {
        payments.add(payment);
        payment.setSale(this);
    }
    
    // ✅ PaymentEntity 리스트를 받도록 변경
    public void addPayments(List<? extends PaymentEntity> newPayments) {
        newPayments.forEach(this::addPayment);
    }
    
    public void recalculateAmounts() {
        calculateTotalAmount();
        calculateDiscountAmount();
        calculatePayableAmount();
    }
    
    public void complete() {
        validateState();
        validatePayments();
        this.status = SaleStatus.COMPLETED;
        recalculateAmounts();
    }
    
    public void cancel(String reason) {
        if (status == SaleStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed order.");
        }
        if (status == SaleStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled.");
        }
        this.status = SaleStatus.CANCELLED;
        this.memo = reason != null ? reason : this.memo;
    }
    
    public void refund() {
        if (status != SaleStatus.COMPLETED) {
            throw new IllegalStateException("Only completed orders can be refunded.");
        }
        this.status = SaleStatus.REFUNDED;
    }
    
    public boolean isPending() {
        return status == SaleStatus.PENDING;
    }
    
    public boolean isCompleted() {
        return status == SaleStatus.COMPLETED;
    }
    
    public boolean isCancelled() {
        return status == SaleStatus.CANCELLED;
    }
    
    public boolean isRefunded() {
        return status == SaleStatus.REFUNDED;
    }
    
    public void assignReceiptNo(String receiptNo) {
        // 이미 할당된 영수증 번호가 있는 경우 (덮어쓰기 금지)
        if (this.receiptNo != null) {
            throw ExceptionUtils.receiptNumberDuplicate(this.receiptNo);
        }
        
        // 영수증 번호 유효성 검증
        if (receiptNo == null || receiptNo.isBlank()) {
            throw ExceptionUtils.missingField("receiptNo");
        }
        

        this.receiptNo = receiptNo;
    }
    
    public void applyDiscounts(List<Discount> discounts) {
        if (discounts == null || discounts.isEmpty()) {
            return;
        }
        
        BigDecimal totalDiscount = discounts.stream()
                .map(Discount::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.discountAmount = totalDiscount;
        calculatePayableAmount();
    }

    // =========================
    // Private Methods
    // =========================
    
    private void calculateTotalAmount() {

        items.forEach(item -> log.error("{}", item));
        
        this.totalAmount = items.stream()
                .map(SaleItem::getTotalAmountBeforeDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private void calculateDiscountAmount() {
        this.discountAmount = items.stream()
                    .map(SaleItem::getDiscountAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private void calculatePayableAmount() {
        this.finalAmount = this.totalAmount.subtract(this.discountAmount);
    }
    
    private void validateState() {
        if (status != SaleStatus.PENDING) {
            throw new IllegalStateException(
                String.format("Order is not in PENDING state. Current state: %s", status)
            );
        }
    }
    
    // ✅ PaymentEntity 타입으로 검증
    public void validatePayments() {
        if (payments.isEmpty()) {
            throw new IllegalStateException("At least one payment is required.");
        }
        BigDecimal totalPaid = payments.stream()
                .map(PaymentEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalPaid.compareTo(this.finalAmount) != 0) {
            throw new IllegalStateException(
                String.format("total amount (%s) does not match final amount (%s).",
                    totalPaid, this.finalAmount)
            );
        }
    }
    

}