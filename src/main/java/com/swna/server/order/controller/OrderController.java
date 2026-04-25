package com.swna.server.order.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.order.dto.request.OrderRequest;
import com.swna.server.order.dto.request.PaymentRequest;
import com.swna.server.order.usecase.CreateOrderUseCase;
import com.swna.server.order.usecase.PayOrderUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;

    // =========================
    // 주문 생성
    // =========================
    @PostMapping
    public Long createOrder(@RequestBody OrderRequest request) {

        return createOrderUseCase.execute(request);
    }

    // =========================
    // 결제 처리
    // =========================
    @PostMapping("/{orderId}/pay")
    public void payOrder(@PathVariable Long orderId, @RequestBody PaymentRequest request) {

        payOrderUseCase.execute(orderId, request);
    }
}
