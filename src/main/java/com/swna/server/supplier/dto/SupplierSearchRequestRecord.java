package com.swna.server.supplier.dto;

public record SupplierSearchRequestRecord(
    String keyword,
    Boolean activeOnly,
    Integer page,
    Integer size
) {
    public boolean hasKeyword() {
        return keyword != null && !keyword.isBlank();
    }
    
    public int getPageOrDefault() {
        return page != null && page >= 0 ? page : 0;
    }
    
    public int getSizeOrDefault() {
        return size != null && size > 0 ? size : 20;
    }
    
    // 빌더 패턴 스타일의 wither 메서드
    public SupplierSearchRequestRecord withKeyword(String keyword) {
        return new SupplierSearchRequestRecord(keyword, activeOnly, page, size);
    }
    
    public SupplierSearchRequestRecord withPagination(int page, int size) {
        return new SupplierSearchRequestRecord(keyword, activeOnly, page, size);
    }
}
