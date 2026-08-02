package com.swna.server.unpack.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Sangwoon Na on 2021. 02. 13..
 * Refactored: CRUD 사용을 위한 Builder/연관관계 편의 메서드 추가
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Unpack {

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

    private LocalDateTime unpacked; // 주문시간

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "unpack", orphanRemoval = true)
    private List<UnpackItem> items = new ArrayList<>();

    private String invoice;
    private String supplierAbbr;
    private String comment;
    private Double amount;
    private Boolean sync;

    @Builder
    public Unpack(Long id, LocalDateTime unpacked, String invoice, String supplierAbbr,
                  String comment, Double amount, Boolean sync) {
        this.id = id;
        this.unpacked = unpacked;
        this.invoice = invoice;
        this.supplierAbbr = supplierAbbr;
        this.comment = comment;
        this.amount = amount;
        this.sync = sync;
        this.items = new ArrayList<>();
    }

    /** 연관관계 편의 메서드: 양방향 무결성 유지 */
    public void addItem(UnpackItem item) {
        items.add(item);
        item.setUnpack(this);
    }

    public void removeItem(UnpackItem item) {
        items.remove(item);
        item.setUnpack(null);
    }

    /** CRUD Update 시 null이 아닌 필드만 부분 갱신 */
    public void applyUpdate(String invoice, String supplierAbbr, String comment,
                             Double amount, Boolean sync, LocalDateTime unpacked) {
        if (invoice != null) this.invoice = invoice;
        if (supplierAbbr != null) this.supplierAbbr = supplierAbbr;
        if (comment != null) this.comment = comment;
        if (amount != null) this.amount = amount;
        if (sync != null) this.sync = sync;
        if (unpacked != null) this.unpacked = unpacked;
    }

    @Override
    public String toString() {
        return "Unpack [id=" + id + ", invoice=" + invoice + ", supplierAbbr=" + supplierAbbr
                + ", comment=" + comment + ", amount=" + amount + ", sync=" + sync
                + ", itemCount=" + (items == null ? 0 : items.size()) + "]";
    }
}