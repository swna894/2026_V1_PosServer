package com.swna.server.order.repository;

import com.swna.server.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // =========================
    // Query Methods (필요한 것만)
    // =========================

    Optional<Order> findByReceiptNo(String receiptNo);
}
