package com.swna.server.unpack.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UnpackItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    @CreatedDate
    @Column(name = "created", updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    @LastModifiedDate
    @Column(name = "updated", updatable = true)
    private LocalDateTime updated;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "unpack_id")
    private Unpack unpack;

    private String invoice;
    private String barcode;
    private String code;
    private String description;
    private String supplier;
    private String category;
    private String abbr;
    private String comment;

    private Integer qty;
    private Integer stock;
    @Column(columnDefinition = "Integer default 12")
    private Integer minOrderQty;
    @Column(columnDefinition = "Integer default 6")
    private Integer minStock;

    private Double amount;
    @Column(columnDefinition = "double default 0")
    private Double pricein;
    @Column(columnDefinition = "double default 0")
    private Double priceout;
    @Column(columnDefinition = "double default 0")
    private Double oldPricein;

    private Boolean confirm;
    private Boolean isSaved;
    private Boolean isNew;

    @Builder
    public UnpackItem(Long id, String invoice, String barcode, String code, String description,
                       String supplier, String category, String abbr, String comment,
                       Integer qty, Integer stock, Integer minOrderQty, Integer minStock,
                       Double amount, Double pricein, Double priceout, Double oldPricein,
                       Boolean confirm, Boolean isSaved, Boolean isNew) {
        this.id = id;
        this.invoice = invoice;
        this.barcode = barcode;
        this.code = code;
        this.description = description;
        this.supplier = supplier;
        this.category = category;
        this.abbr = abbr;
        this.comment = comment;
        this.qty = qty;
        this.stock = stock;
        this.minOrderQty = minOrderQty;
        this.minStock = minStock;
        this.amount = amount;
        this.pricein = pricein;
        this.priceout = priceout;
        this.oldPricein = oldPricein;
        this.confirm = confirm;
        this.isSaved = isSaved;
        this.isNew = isNew;
    }

    /** CRUD Update 시 null이 아닌 필드만 부분 갱신 */
    public void applyUpdate(UnpackItem source) {
        if (source.barcode != null) this.barcode = source.barcode;
        if (source.code != null) this.code = source.code;
        if (source.description != null) this.description = source.description;
        if (source.supplier != null) this.supplier = source.supplier;
        if (source.category != null) this.category = source.category;
        if (source.abbr != null) this.abbr = source.abbr;
        if (source.comment != null) this.comment = source.comment;
        if (source.qty != null) this.qty = source.qty;
        if (source.stock != null) this.stock = source.stock;
        if (source.minOrderQty != null) this.minOrderQty = source.minOrderQty;
        if (source.minStock != null) this.minStock = source.minStock;
        if (source.amount != null) this.amount = source.amount;
        if (source.pricein != null) this.pricein = source.pricein;
        if (source.priceout != null) this.priceout = source.priceout;
        if (source.oldPricein != null) this.oldPricein = source.oldPricein;
        if (source.confirm != null) this.confirm = source.confirm;
        if (source.isSaved != null) this.isSaved = source.isSaved;
        if (source.isNew != null) this.isNew = source.isNew;
    }

    @Override
    public String toString() {
        return "UnpackItem [id=" + id + ", unpackId=" + (unpack != null ? unpack.getId() : null)
                + ", invoice=" + invoice + ", barcode=" + barcode + ", code=" + code
                + ", description=" + description + ", supplier=" + supplier + ", abbr=" + abbr
                + ", qty=" + qty + ", amount=" + amount + ", pricein=" + pricein
                + ", comment=" + comment + ", category=" + category + ", confirm=" + confirm + "]";
    }
}