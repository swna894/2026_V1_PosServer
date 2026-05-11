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

    @Column(precision = 19, scale = 2)
    private BigDecimal globalDiscountAmount;  // 전체 할인 금액 (표시용, 계산 미사용)

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
        
        // 전체 할인은 표시 목적으로만 저장 (계산에는 사용하지 않음)
        if (discounts != null && !discounts.isEmpty()) {
            sale.globalDiscountAmount = discounts.stream()
                    .map(Discount::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
            log.debug("Global discount amount (for display only): {}", sale.globalDiscountAmount);
        }
        
        // items의 값으로만 금액 계산 (클라이언트가 이미 할인 분배함)
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
    
    public void addPayment(PaymentEntity payment) {
        payments.add(payment);
        payment.setSale(this);
    }
    
    public void addPayments(List<? extends PaymentEntity> newPayments) {
        newPayments.forEach(this::addPayment);
    }
    
    /**
     * 금액 재계산 (item들의 값을 단순 합산)
     * 클라이언트가 이미 모든 할인을 item에 분배했으므로 단순 합계만 계산
     */
    public void recalculateAmounts() {
        // originalAmount = 모든 item의 (salePrice + discountPrice) × quantity
        this.originalAmount = items.stream()
                .map(SaleItem::getOriginalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        
        // discountAmount = 모든 item의 discountPrice × quantity
        this.discountAmount = items.stream()
                .map(SaleItem::getDiscountAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        
        // saleAmount = 모든 item의 salePrice × quantity (클라이언트 최종 금액)
        this.saleAmount = items.stream()
                .map(SaleItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 전체 할인 적용
     * 클라이언트가 이미 items에 할인을 분배했으므로 아무것도 하지 않음
     * (유지보수를 위해 빈 메서드로 남겨둠)
     */
    public void applyDiscounts(List<Discount> discounts) {
        if (discounts != null && !discounts.isEmpty()) {
            log.debug("Global discounts ignored - already distributed to items: {}", discounts);
        }
        // 아무것도 하지 않음 (이미 items에 할인 분배됨)
    }
    
    public void complete() {
        validateState();
        validatePayments();
        this.status = SaleStatus.COMPLETED;
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
        if (this.receiptNo != null) {
            throw ExceptionUtils.receiptNumberDuplicate(this.receiptNo);
        }
        
        if (receiptNo == null || receiptNo.isBlank()) {
            throw ExceptionUtils.missingField("receiptNo");
        }

        this.receiptNo = receiptNo;
    }

    // =========================
    // Private Methods
    // =========================
    
    private void validateState() {
        if (status != SaleStatus.PENDING) {
            throw new IllegalStateException(
                String.format("Order is not in PENDING state. Current state: %s", status)
            );
        }
    }
    
    public void validatePayments() {
        if (payments.isEmpty()) {
            throw new IllegalStateException("At least one payment is required.");
        }
        
        BigDecimal totalPaid = payments.stream()
                .map(PaymentEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        
        // 클라이언트의 saleAmount와 결제 금액 비교
        if (totalPaid.compareTo(this.saleAmount) != 0) {
            throw new IllegalStateException(
                String.format("Payment mismatch: total paid=%s, sale amount=%s, difference=%s",
                    totalPaid.toPlainString(), 
                    this.saleAmount.toPlainString(),
                    totalPaid.subtract(this.saleAmount).toPlainString())
            );
        }
    }
}