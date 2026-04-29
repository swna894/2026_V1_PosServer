package com.swna.server.sale.entity;

public enum SaleStatus {

    CREATED,
    PAID,
    CANCELLED,
    REFUNDED;

    public boolean canChangeTo(SaleStatus target) {

        return switch (this) {
            case CREATED -> target == PAID || target == CANCELLED;
            case PAID -> target == REFUNDED;
            case CANCELLED -> false;
            case REFUNDED -> false;
        };
    }
}
