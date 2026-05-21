package com.swna.server.sale.dto.response;

import java.math.BigDecimal;
import com.swna.server.sale.entity.SaleItem;

/**
 * 판매 아이템 응답 DTO (Record 타입)
 * JavaFX 클라이언트의 SaleItemDto 스펙에 맞추어 리팩토링 완료
 */
public record SaleItemResponse(
        String id,            // 클라이언트 스펙(String)에 맞춤
        String barcode,
        BigDecimal discountPrice,
        BigDecimal cost,
        BigDecimal salePrice,
        int quantity,
        String supplier,
        String comment
) {

    /**
     * 컴팩트 생성자: 데이터 검증 및 기본값(Null 방어) 세팅
     */
    public SaleItemResponse {
        if (quantity < 0) quantity = 0;
        if (discountPrice == null) discountPrice = BigDecimal.ZERO;
        if (cost == null) cost = BigDecimal.ZERO;
        if (salePrice == null) salePrice = BigDecimal.ZERO;
        if (comment == null) comment = "";
    }

    /**
     * 엔티티 및 추가 정보를 바탕으로 정적 팩토리 메서드 구현
     * 기존 SaleItem 엔티티의 메서드명을 기반으로 매핑했습니다.
     */
    public static SaleItemResponse from(SaleItem item, BigDecimal cost, String supplier) {
        return new SaleItemResponse(
                item.getProductId() != null ? String.valueOf(item.getProductId()) : null, // Long -> String 변환
                item.getBarcode(),
                item.getDiscountPrice(),
                cost, // 엔티티에 cost/supplier가 없다면 외부 파라미터나 기본값으로 처리하도록 구성
                item.getSalePrice(),
                item.getQuantity(),
                supplier,
                item.getComment()
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 빌더 패턴 구현 (내부 클래스)
     */
    public static class Builder {
        private String id;
        private String barcode;
        private BigDecimal discountPrice = BigDecimal.ZERO;
        private BigDecimal cost = BigDecimal.ZERO;
        private BigDecimal salePrice = BigDecimal.ZERO;
        private int quantity;
        private String supplier;
        private String comment;

        public Builder id(String id) { this.id = id; return this; }
        public Builder barcode(String barcode) { this.barcode = barcode; return this; }
        public Builder discountPrice(BigDecimal discountPrice) { this.discountPrice = discountPrice; return this; }
        public Builder cost(BigDecimal cost) { this.cost = cost; return this; }
        public Builder salePrice(BigDecimal salePrice) { this.salePrice = salePrice; return this; }
        public Builder quantity(int quantity) { this.quantity = quantity; return this; }
        public Builder supplier(String supplier) { this.supplier = supplier; return this; }
        public Builder comment(String comment) { this.comment = comment; return this; }

        public SaleItemResponse build() {
            return new SaleItemResponse(
                    this.id,
                    this.barcode,
                    this.discountPrice,
                    this.cost,
                    this.salePrice,
                    this.quantity,
                    this.supplier,
                    this.comment
            );
        }
    }
}