package com.swna.server.payment.entity;

import java.math.BigDecimal;

import com.swna.server.sale.entity.Sale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "payments")
public abstract class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Sale sale;

    @Column(nullable = false)
    private BigDecimal amount;

    // =========================
    // Common behavior
    // =========================

    public void assignOrder(Sale sale) {
        this.sale = sale;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    protected void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}