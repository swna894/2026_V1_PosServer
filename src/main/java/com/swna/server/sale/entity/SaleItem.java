package com.swna.server.sale.entity;

import java.math.BigDecimal;

import com.swna.server.product.entity.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sale_items")
public class SaleItem {

    @Id     
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    // =========================
    // Snapshot (핵심)
    // =========================
    private String productBarcode;
    private String productDescription;
    private BigDecimal price;
    
    private int quantity;

    // =========================
    // Factory
    // =========================
    public static SaleItem of(Product product, int quantity) {

        SaleItem item = new SaleItem();

        // relation (optional)
        item.product = product;

        // snapshot (핵심)
        item.productBarcode = product.getBarcode();
        item.productDescription = product.getDescription();
        item.price = product.getPrice();

        item.quantity = quantity;

        return item;
    }
    // =========================
    // Business
    // =========================

    public BigDecimal getSubTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public void assignOrder(Sale sale) {
        this.sale = sale;
    }
}
