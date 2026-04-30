package com.swna.server.sale.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swna.server.sale.entity.Sale;

import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    // =========================
    // Query Methods (필요한 것만)
    // =========================

    Optional<Sale> findByReceiptNo(String receiptNo);
}
