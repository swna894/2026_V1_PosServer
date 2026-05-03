package com.swna.server.product.dto;

import java.math.BigDecimal;

public record ProductLabelDto( String barcode, String description, BigDecimal price ) {}
