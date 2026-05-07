package com.swna.server.sale.dto.response;

import java.math.BigDecimal;

import com.swna.server.sale.entity.Sale;
import com.swna.server.sale.entity.SaleStatus;

public record SaleResponse(
        Long id,
        String receiptNo,
        String status,
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        BigDecimal finalAmount
) {

    /**
     * Sale 엔티티로부터 Response 생성 (권장 방식)
     */
    public static SaleResponse from(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getReceiptNo(),
                sale.getStatus() != null ? sale.getStatus().name() : SaleStatus.PENDING.name(),
                sale.getTotalAmount(),
                sale.getDiscountAmount(),
                sale.getFinalAmount()
        );
    }
    
    /**
     * 간단한 변환 (Product 정보 없이 - 개발/테스트용)
     */
    public static SaleResponse of(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getReceiptNo(),
                sale.getStatus().name(),
                sale.getTotalAmount(),
                sale.getDiscountAmount(),
                sale.getFinalAmount()
        );
    }
}