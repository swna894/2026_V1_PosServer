package com.swna.server.sale.dto.request;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.swna.server.sale.entity.DiscountType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record SaleRequest(
    @NotEmpty(message = "At least one sale item is required.")
    @Valid List<SaleItemRequest> items,

    @NotEmpty(message = "At least one payment record is required.")
    @Valid List<PaymentRequest> payments,

    @Valid List<DiscountRequest> discounts
) {

    // =========================
    // Static Factory Methods
    // =========================
    
    public static SaleRequest of(List<SaleItemRequest> items, List<PaymentRequest> payments) {
        return new SaleRequest(items, payments, null);
    }
    
    public static SaleRequest withDiscounts(List<SaleItemRequest> items, 
                                            List<PaymentRequest> payments, 
                                            List<DiscountRequest> discounts) {
        return new SaleRequest(items, payments, discounts);
    }
    
    public static SaleRequest empty() {
        return new SaleRequest(Collections.emptyList(), Collections.emptyList(), null);
    }

    // =========================
    // Constructor with Normalization
    // =========================
    
    public SaleRequest {
        // 정규화: null을 빈 리스트로 변환
        if (discounts == null) {
            discounts = Collections.emptyList();
        }
        
        // 불변 리스트로 변환
        items = List.copyOf(items);
        payments = List.copyOf(payments);
        discounts = List.copyOf(discounts);
    }

    // =========================
    // Business Methods
    // =========================
    
    public boolean hasDiscounts() {
        return !discounts.isEmpty();
    }
    
    public boolean hasMultiplePayments() {
        return payments.size() > 1;
    }
    
    public boolean isSingleItemSale() {
        return items.size() == 1;
    }
    
    public int getTotalItemCount() {
        return items.size();
    }
    
    public int getTotalQuantity() {
        return items.stream()
                .mapToInt(SaleItemRequest::quantity)
                .sum();
    }
    
    public BigDecimal getTotalPaymentsAmount() {
        return payments.stream()
                .map(PaymentRequest::amount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getTotalDiscountAmount() {
        return discounts.stream()
                .map(DiscountRequest::value)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public List<SaleItemRequest> getNonZeroQuantityItems() {
        return items.stream()
                .filter(item -> item.quantity() > 0)
                .toList();
    }
    
    public Optional<PaymentRequest> getFirstPayment() {
        return payments.isEmpty() ? Optional.empty() : Optional.of(payments.get(0));
    }
    
    public boolean hasCashPayment() {
        return payments.stream().anyMatch(PaymentRequest::isCash);
    }
    
    public boolean hasCardPayment() {
        return payments.stream().anyMatch(PaymentRequest::isCard);
    }
    
    public List<DiscountRequest> getPercentDiscounts() {
        return discounts.stream()
                .filter(d -> d.type() == DiscountType.PERCENT)
                .toList();
    }
    
    public List<DiscountRequest> getFixedDiscounts() {
        return discounts.stream()
                .filter(d -> d.type() == DiscountType.AMOUNT)
                .toList();
    }

    // =========================
    // Validation Methods
    // =========================
    
    public void validate() {
        validateQuantities();
        validateDiscounts();
        validatePayments();
    }
    
    private void validateQuantities() {
        boolean hasInvalidQuantity = items.stream()
                .anyMatch(item -> item.quantity() <= 0);
        
        if (hasInvalidQuantity) {
            throw new IllegalArgumentException("All quantities must be greater than 0");
        }
    }
    
    private void validateDiscounts() {
        boolean hasInvalidDiscount = discounts.stream()
                .anyMatch(d -> d.value() != null && d.value().compareTo(BigDecimal.ZERO) < 0);
        
        if (hasInvalidDiscount) {
            throw new IllegalArgumentException("Discount values cannot be negative");
        }
        
        boolean hasInvalidPercent = discounts.stream()
                .filter(d -> d.type() == DiscountType.PERCENT)
                .anyMatch(d -> d.value() != null && d.value().compareTo(BigDecimal.valueOf(100)) > 0);
        
        if (hasInvalidPercent) {
            throw new IllegalArgumentException("Percentage discount cannot exceed 100%");
        }
    }
    
    private void validatePayments() {
        boolean hasInvalidAmount = payments.stream()
                .anyMatch(p -> p.amount() != null && p.amount().compareTo(BigDecimal.ZERO) <= 0);
        
        if (hasInvalidAmount) {
            throw new IllegalArgumentException("Payment amounts must be greater than 0");
        }
    }
}