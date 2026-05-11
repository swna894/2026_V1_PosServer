package com.swna.server.sale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.common.response.ApiResponse;
import com.swna.server.sale.dto.request.SaleRequest;
import com.swna.server.sale.dto.response.SaleResponse;
import com.swna.server.sale.usecase.ProcessSaleUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final ProcessSaleUseCase processSaleUseCase;

    /**
     * 주문 + 할인 + 결제 통합 처리 API
     * 클라이언트는 상품, 할인, 결제 정보를 SaleRequest 하나에 담아 전송합니다.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponse>> createSale(@Valid @RequestBody SaleRequest request) {
        log.debug("Received sale creation request");
        

        // 1. 요청 검증 (ExceptionUtils 활용)
        if (request.items() == null || request.items().isEmpty()) {
            throw ExceptionUtils.missingField("items");
        }
        // 2. 각 아이템 검증
        request.items().forEach(item -> {
            if (item.barcode() == null || item.barcode().isBlank()) {
                throw ExceptionUtils.missingField("barcode");
            }
            if (item.quantity() <= 0) {
                throw ExceptionUtils.invalidInput("quantity", "must be greater than 0");
            }
        });
        
        // 3. 판매 처리
        SaleResponse response = processSaleUseCase.execute(request);
            
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("SALE_CREATED", response));
    }
}
