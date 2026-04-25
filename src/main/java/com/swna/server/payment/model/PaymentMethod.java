package com.swna.server.payment.model;

import java.math.BigDecimal;

public interface PaymentMethod {

    BigDecimal getAmount();

}