package com.swna.server.sale.entity;

import java.math.BigDecimal;

import com.swna.server.common.entity.BaseEntity;
import com.swna.server.product.entity.Product;
import com.swna.server.sale.dto.request.SaleItemRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sale_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaleItem extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale  sale;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String barcode;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal priceAtSale; // 판매 시점 단가[cite: 4]

    @Column(nullable = false)
    private BigDecimal discountValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    // 추가 권장 변수: 계산된 최종 금액을 DB에 미리 저장하면 통계 쿼리가 빨라집니다.
    @Column(nullable = false)
    private BigDecimal totalAmount; // (단가 * 수량) - 할인액

    private String comment;

    // =========================
    // 생성 메서드 (Factory)
    // =========================
    
    public static SaleItem of(Product product, SaleItemRequest request) {
        // 할인액 계산 로직 예시 (필요에 따라 서비스 레이어로 이동 가능)
        BigDecimal price = product.getPrice();
        BigDecimal qty = BigDecimal.valueOf(request.quantity());
        BigDecimal totalBeforeDiscount = price.multiply(qty);
        
        // 간단한 할인 계산 예시
        BigDecimal discount = request.discountValue();
        BigDecimal finalAmount = totalBeforeDiscount.subtract(discount); 

        return SaleItem.builder()
                .productId(product.getId())
                .barcode(product.getBarcode())
                .quantity(request.quantity())
                .priceAtSale(price)
                .discountValue(discount) // 이 부분 괄호 오타 수정됨
                .discountType(request.discountType())
                .comment(request.comment())
                .totalAmount(finalAmount)
                .build();
    }

    @Builder
    private SaleItem(Long productId, String barcode, int quantity, BigDecimal priceAtSale,
                    BigDecimal discountValue, DiscountType discountType, BigDecimal totalAmount, String comment) {
        this.productId = productId;
        this.barcode = barcode;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.totalAmount = totalAmount;
        this.comment = comment;
    }

    protected void setSale(Sale sale) {
        this.sale = sale;
    }

    public BigDecimal getTotalAmountBeforeDiscount() {
        return this.priceAtSale.multiply(BigDecimal.valueOf(this.quantity));
    }
}