package com.swna.server.sale.entity;

public enum SaleStatus {
    PENDING("Pending", "Order created but not paid"),
    COMPLETED("Completed", "Order paid and completed"),
    CANCELLED("Cancelled", "Order cancelled before payment"),
    REFUNDED("Refunded", "Order refunded after payment");
    
    private final String displayName;
    private final String description;
    
    SaleStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean canTransitionTo(SaleStatus target) {
        return switch (this) {
            case PENDING -> target == COMPLETED || target == CANCELLED;
            case COMPLETED -> target == REFUNDED;
            case CANCELLED, REFUNDED -> false;
        };
    }
}
