package com.swna.server.sale.dto.request_old;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Refactored SaleItemRequest for Client-Server synchronization.
 */
public record SaleItemRequest(

        @NotNull(message = "Product ID is required")
        Long id,

        @NotBlank(message = "Barcode cannot be empty")
        @Size(min = 3, max = 50, message = "Barcode must be between 3 and 50 characters")
        String barcode,

        @NotNull(message = "Unit discount value is required")
        @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Unit discount must be a valid monetary format (e.g., 10.00)")
        String unitDiscount,

        @Size(max = 255, message = "Comment must not exceed 255 characters")
        String comment,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity
) {}