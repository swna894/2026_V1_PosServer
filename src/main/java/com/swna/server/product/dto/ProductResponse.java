package com.swna.server.product.dto;

import java.math.BigDecimal;

public record ProductResponse(
        String code,
        String barcode,
        String description,
        BigDecimal sellingPrice,
        BigDecimal originalPrice,
        int stock
) {}