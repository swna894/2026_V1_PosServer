package com.swna.server.product.entity;

import java.math.BigDecimal;

import com.swna.server.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(length = 100)
    private String barcode;

    private String abbr;
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private boolean deleted = false; // Soft Delete

    // =========================
    // 생성 메서드 (Factory)
    // =========================
    public static Product create(
            String code,
            String name,
            BigDecimal price,
            String barcode,
            Category category
    ) {
        validate(code, name, price);

        Product product = new Product();
        product.code = code;
        product.name = name;
        product.price = price;
        product.barcode = barcode;
        product.category = category;
        return product;
    }

    // =========================
    // 비즈니스 메서드
    // =========================

    public void changePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        this.price = newPrice;
    }

    public void changeName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        this.name = newName;
    }

    public void assignCategory(Category category) {
        this.category = category;
    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isAvailable() {
        return !deleted;
    }

    // =========================
    // 검증 로직
    // =========================
    private static void validate(String code, String name, BigDecimal price) {

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("상품코드는 필수입니다.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
    }
}