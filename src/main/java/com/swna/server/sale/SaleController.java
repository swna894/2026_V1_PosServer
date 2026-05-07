package com.swna.server.sale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.common.exception.BusinessException;
import com.swna.server.common.exception.ErrorCode;
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
    @PostMapping("/process")
    public ResponseEntity<SaleResponse> processOrder(@RequestBody @Valid SaleRequest request) {
        SaleResponse response = processSaleUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponse>> createSale(@Valid @RequestBody SaleRequest request) {
        log.debug("Received sale creation request with {} items", request.items().size());
        
    
        // 2. 리스트 내역 상세 출력
        if (request.items() != null) {
            request.items().forEach(item -> {
                System.out.println("바코드: " + item.barcode());
            });
        }

        try {
            // Request validation (additional business rules if needed)
            request.validate();
            
            SaleResponse response = processSaleUseCase.execute(request);
            System.out.println("🚩 회신 데이터 전체: " + response);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "SALE_CREATED", "Sale created successfully"));
            
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR, "An unexpected error occurred"));
        }
    }
}
