package com.swna.server.sale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swna.server.sale.entity.Sale;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    // =========================
    // Query Methods (필요한 것만)
    // =========================

    Optional<Sale> findByReceiptNo(String receiptNo);

    // =========================
    // Sale Status (필요한 것만)
    // =========================
      // 기간별 조회
    @Query("SELECT DISTINCT s FROM Sale s " +
           "LEFT JOIN FETCH s.payments p " +
           "WHERE p.paymentDateTime BETWEEN :startDate AND :endDate " +
           "ORDER BY p.paymentDateTime DESC")
    List<Sale> findByPaymentDateTimeBetweenWithPayments(@Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);
}
