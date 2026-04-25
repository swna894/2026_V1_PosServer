package com.swna.server.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swna.server.payment.domain.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
}
