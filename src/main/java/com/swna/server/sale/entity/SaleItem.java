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
import lombok.ToString;

@Entity
@Table(name = "sale_items")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaleItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String barcode;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal salePrice; // 판매 시점 단가 (클라이언트 값)

    @Column(nullable = false)
    private BigDecimal discountPrice; // 아이템 할인액 (클라이언트 값)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private BigDecimal totalAmount; // 최종 금액 = salePrice × quantity

    private String comment;

    // =========================
    // 생성 메서드 (Factory)
    // =========================
    
    public static SaleItem of(Product product, SaleItemRequest request) {
        BigDecimal quantity = BigDecimal.valueOf(request.quantity());
        BigDecimal totalAmount = request.sellingPrice().multiply(quantity);
        
        return SaleItem.builder()
                .productId(product.getId())
                .barcode(product.getBarcode())
                .quantity(request.quantity())
                .salePrice(request.sellingPrice())      // ✅ 클라이언트 값 그대로
                .discountValue(request.discountValue()) // ✅ 클라이언트 값 그대로
                .discountType(request.discountType())
                .comment(request.comment())
                .totalAmount(totalAmount)               // ✅ salePrice × quantity
                .build();
    }

    @Builder
    private SaleItem(Long productId, String barcode, int quantity, BigDecimal salePrice,
                    BigDecimal discountValue, DiscountType discountType, BigDecimal totalAmount, String comment) {
        this.productId = productId;
        this.barcode = barcode;
        this.quantity = quantity;
        this.salePrice = salePrice;
        this.discountPrice = discountValue;
        this.discountType = discountType;
        this.totalAmount = totalAmount;
        this.comment = comment;
    }

    protected void setSale(Sale sale) {
        this.sale = sale;
    }

    // =========================
    // 계산 메서드 (집계용)
    // =========================
    
    /**
     * 원래 금액 = (salePrice + discountPrice) × quantity
     * 할인 전 원래 가격을 계산 (화면 표시용)
     */
    public BigDecimal getOriginalAmount() {
        BigDecimal originalUnitPrice = this.salePrice.add(this.discountPrice);
        return originalUnitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }
    
    /**
     * 아이템 할인 총액 = discountPrice × quantity
     */
    public BigDecimal getDiscountAmount() {
        return this.discountPrice.multiply(BigDecimal.valueOf(this.quantity));
    }
}