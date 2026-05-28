package com.swna.server.sale.entity;

public enum SaleStatus {
    PENDING("Pending", "Order created but not paid"),
    COMPLETED("Completed", "Order paid and completed"),
    CANCELLED("Cancelled", "Order cancelled before payment"),
    REFUNDED("Refunded", "Order refunded after payment"),
    DELETED("Deleted", "Order has been deleted");
    
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
    
    /**
     * 상태 전이 가능 여부 확인
     * @param target 전환하려는 대상 상태
     * @return 전환 가능하면 true, 불가능하면 false
     */
    public boolean canTransitionTo(SaleStatus target) {
        if (this == target) {
            return false;  // 같은 상태로 전이 불가
        }
        
        return switch (this) {
            case PENDING -> target == COMPLETED || target == CANCELLED;
            case COMPLETED -> target == REFUNDED || target == DELETED;
            case CANCELLED -> target == DELETED;  // 취소된 주문은 삭제 가능
            case REFUNDED, DELETED -> false;  // 최종 상태
        };
    }
}