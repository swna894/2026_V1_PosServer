package com.swna.server.sale.domain;

import java.math.BigDecimal;

public interface PaymentMethod {
    BigDecimal getAmount();
    String getType();
    boolean validate();
}