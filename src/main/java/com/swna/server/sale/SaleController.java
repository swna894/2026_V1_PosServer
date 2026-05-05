package com.swna.server.sale;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.sale.dto.request_old.PaymentRequest;
import com.swna.server.sale.dto.request_old.SaleRequest;
import com.swna.server.sale.dto.response.SaleResponse;
import com.swna.server.sale.usecase.CreateSaleUseCase;
import com.swna.server.sale.usecase.PaySaleUseCase;
import com.swna.server.sale.usecase.ProcessSaleUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final CreateSaleUseCase createOrderUseCase;
    private final PaySaleUseCase payOrderUseCase;
    private final ProcessSaleUseCase processSaleUseCase;

    /**
     * 주문 + 할인 + 결제 통합 처리 API
     * 클라이언트는 상품, 할인, 결제 정보를 SaleRequest 하나에 담아 전송합니다.
     */
    @PostMapping("/process")
    public ResponseEntity<SaleResponse> processOrder(@RequestBody @Valid SaleRequest request) {
        SaleResponse response = processSaleUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    // =========================
    // 주문 생성
    // =========================
    @PostMapping
    public Long createOrder(@RequestBody SaleRequest request) {
        return createOrderUseCase.execute(request);
    }

    // =========================
    // 결제 처리
    // =========================
    @PostMapping("/{saleId}/pay")
    public void payOrder(@PathVariable Long saleId, @RequestBody PaymentRequest request) {

        payOrderUseCase.execute(saleId, request);
    }


}
