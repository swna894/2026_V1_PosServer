package com.swna.server.product.entity;

import java.math.BigDecimal;

import com.swna.server.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(length = 100)
    private String barcode;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    private String abbr;
    private String comment;

    private String category;

    @Column(nullable = false)
    private boolean deleted = false; // Soft Delete

    // =========================
    // 생성 메서드 (Factory)
    // =========================
    public static Product create(
            String code,
            String description,
            BigDecimal price,
            BigDecimal cost,
            String barcode,
            String category
    ) {
        validate(code, description, price);

        Product product = new Product();
        product.code = code;
        product.description = description;
        product.price = price;
        product.cost = price;
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

    public void changeName(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        this.description = description;
    }

    public void assignCategory(String category) {
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

    // =========================
    // 🔥 핵심: Category 변경 메서드
    // =========================
    public void changeCategory(String category) {
        if (category == null) {
            throw new IllegalArgumentException("category must not be null");
        }
        this.category = category;
    }
}