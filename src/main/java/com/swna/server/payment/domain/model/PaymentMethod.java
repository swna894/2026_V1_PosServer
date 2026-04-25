package com.swna.server.payment.domain.model;

import java.math.BigDecimal;

public interface PaymentMethod {

    BigDecimal getAmount();

}