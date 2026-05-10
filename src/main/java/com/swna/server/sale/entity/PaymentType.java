package com.swna.server.sale.entity;

public enum PaymentType {
    CASH("CASH"),
    CARD("CARD"),
    CASHOUT("CASHOUT");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentType from(String value) {
        for (PaymentType type : PaymentType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown payment type: " + value);
    }
}