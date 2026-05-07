package com.swna.server.sale.dto.request;

import java.math.BigDecimal;

import com.swna.server.sale.entity.DiscountType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SaleItemRequest(

    @NotNull(message = "Barcode cannot be empty.")
    String barcode,

    @Min(value = 1, message = "Quantity must be at least 1.")
    int quantity,

    @NotNull(message = "Discount value is required. (Use 0 if none)")
    BigDecimal discountValue,

    @NotNull(message = "Discount type is required. (e.g., AMOUNT or PERCENT)")
    DiscountType discountType, 

    @Size(max = 255, message = "Comment must not exceed 255 characters.")
    String comment
    
) {
       public boolean hasDiscount() {
        return discountValue != null && discountValue.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        if (!hasDiscount()) {
            return originalPrice;
        }
        
        return switch (discountType) {
            case DiscountType.PERCENT -> originalPrice.multiply(BigDecimal.valueOf(100 - discountValue.doubleValue()))
                    .divide(BigDecimal.valueOf(100));
            case DiscountType.AMOUNT -> originalPrice.subtract(discountValue);
            default -> originalPrice;
        };
    }
}
