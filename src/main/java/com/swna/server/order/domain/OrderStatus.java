package com.swna.server.order.domain;

public enum OrderStatus {

    CREATED,
    PAID,
    CANCELLED,
    REFUNDED;

    public boolean canChangeTo(OrderStatus target) {

        return switch (this) {
            case CREATED -> target == PAID || target == CANCELLED;
            case PAID -> target == REFUNDED;
            case CANCELLED -> false;
            case REFUNDED -> false;
        };
    }
}
