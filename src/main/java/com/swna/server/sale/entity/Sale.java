package com.swna.server.sale.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private BigDecimal originalAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saleAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal costAmount;

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
        sale.recalculateAmounts();
        return sale;
    }

    public static Sale create(List<SaleItem> items) {
        return create(items, List.of());
    }

    // =========================
    // Business Methods (개선된 버전)
    // =========================
    
    public void addItem(SaleItem item) {
        items.add(item);
        item.setSale(this);
        recalculateAmounts();
    }
    
    public void addItems(List<SaleItem> newItems) {
        newItems.forEach(this::addItem);
    }
    
    public void addPayment(PaymentEntity payment) {
        payments.add(payment);
        payment.setSale(this);
    }
    
    public void addPayments(List<? extends PaymentEntity> newPayments) {
        newPayments.forEach(this::addPayment);
    }
    
    /**
     * 금액 재계산 (item들의 값을 단순 합산)
     */
    public void recalculateAmounts() {
        this.originalAmount = items.stream()
                .map(SaleItem::getOriginalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        
        this.discountAmount = items.stream()
                .map(SaleItem::getDiscountAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        
        this.saleAmount = items.stream()
                .map(SaleItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        this.costAmount = items.stream()
                .map(SaleItem::getCostAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * ✅ 개선: canTransitionTo 활용한 완료 처리
     */
    public void complete() {
        // DELETED 상태면 아무것도 하지 않음
        if (this.status == SaleStatus.DELETED) {
            log.debug("Sale {} is DELETED, keeping DELETED status", this.id);
            return;
        }

        if (!status.canTransitionTo(SaleStatus.COMPLETED)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to COMPLETED", status)
            );
        }
        validatePayments();
        this.status = SaleStatus.COMPLETED;
        log.info("Sale {} completed", this.id);
    }
    
    /**
     * ✅ 개선: canTransitionTo 활용한 취소 처리
     */
    public void cancel(String reason) {
        if (!status.canTransitionTo(SaleStatus.CANCELLED)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to CANCELLED", status)
            );
        }
        this.status = SaleStatus.CANCELLED;
        this.memo = reason != null ? reason : this.memo;
        log.info("Sale {} cancelled. Reason: {}", this.id, this.memo);
    }
    
    /**
     * ✅ 개선: canTransitionTo 활용한 환불 처리
     */
    public void refund() {
        if (!status.canTransitionTo(SaleStatus.REFUNDED)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to REFUNDED", status)
            );
        }
        this.status = SaleStatus.REFUNDED;
        log.info("Sale {} refunded", this.id);
    }
    
    /**
     * ✅ 추가: canTransitionTo 활용한 삭제 처리
     */
    public void delete() {
        if (!status.canTransitionTo(SaleStatus.DELETED)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to DELETED", status)
            );
        }
        this.status = SaleStatus.DELETED;
        this.memo = "Deleted: " + (this.memo != null ? this.memo : "");
        log.info("Sale {} marked as DELETED", this.id);
    }
    
    public void applyDiscounts(List<Discount> discounts) {
        if (discounts != null && !discounts.isEmpty()) {
            log.debug("Global discounts ignored - already distributed to items: {}", discounts);
        }
    }
    
    public void assignReceiptNo(String receiptNo) {
        if (this.receiptNo != null) {
            throw ExceptionUtils.receiptNumberDuplicate(this.receiptNo);
        }
        
        if (receiptNo == null || receiptNo.isBlank()) {
            throw ExceptionUtils.missingField("receiptNo");
        }

        this.receiptNo = receiptNo;
    }

    // =========================
    // Validation Methods
    // =========================
    
    public void validatePayments() {
        if (payments.isEmpty()) {
            throw new IllegalStateException("At least one payment is required.");
        }
        
        BigDecimal totalPaid = payments.stream()
                .map(PaymentEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        
        if (totalPaid.compareTo(this.saleAmount) != 0) {
            throw new IllegalStateException(
                String.format("Payment mismatch: total paid=%s, sale amount=%s, difference=%s",
                    totalPaid.toPlainString(), 
                    this.saleAmount.toPlainString(),
                    totalPaid.subtract(this.saleAmount).toPlainString())
            );
        }
    }
    
    // =========================
    // Convenience Methods
    // =========================
    
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
    
    public boolean isDeleted() {
        return status == SaleStatus.DELETED;
    }

    /**
     * 상태를 직접 설정 (특수 케이스용)
     * 일반 비즈니스 로직에서는 사용하지 않음
     */
    public void setStatusDirectly(SaleStatus newStatus) {
        // 상태 전이 규칙 없이 직접 설정 (DELETE 케이스 등 특수 상황)
        this.status = newStatus;
        log.info("Sale {} status directly set to {}", this.id, newStatus);
    }
}