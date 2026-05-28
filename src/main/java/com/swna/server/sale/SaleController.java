package com.swna.server.sale;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.common.response.ApiResponse;
import com.swna.server.sale.dto.request.SaleRequest;
import com.swna.server.sale.dto.response.SaleItemResponse;
import com.swna.server.sale.dto.response.SaleResponse;
import com.swna.server.sale.usecase.ProcessSaleUseCase;
import com.swna.server.sale.usecase.SaleItemService;
import com.swna.server.sale_status.dto.SaleDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final ProcessSaleUseCase processSaleUseCase;
    private final SaleItemService saleItemService;




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




    @GetMapping("/date-range")
    public ApiResponse<List<SaleDto>> getSalesByDateRange(
             @RequestParam("startDate")  LocalDateTime startDate,
             @RequestParam("endDate") LocalDateTime endDate) {
        List<SaleDto> sales = processSaleUseCase.getSalesByDateRange(startDate, endDate);

        log.info("result count: {}", sales == null ? "null" : sales.size());
        if (sales != null && !sales.isEmpty()) {
            log.info("first sale id: {}", sales.get(0).getId());
            log.info("first sale receiptNo: {}", sales.get(0).getReceiptNo());
            log.info("first sale saleAmount: {}", sales.get(0).getSaleAmount());
            
            // 상세 로그 (JSON 형식)
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(sales);
                log.info("sales JSON: {}", json);
            } catch (Exception e) {
                log.error("Failed to convert to JSON", e);
            }
        } else {
            log.warn("No sales found for date range: {} ~ {}", startDate, endDate);
        }
        
        log.info("===== getSalesByDateRange finished =====");

        return ApiResponse.success(sales);
    }




    /**
     * 특정 판매 건(saleId)의 아이템 목록을 조회합니다.
     */
    @GetMapping("/{saleId}/items")
    public ResponseEntity<ApiResponse<List<SaleItemResponse>>> getSaleItems(@PathVariable("saleId") Long saleId) {
        
        List<SaleItemResponse> responseData = saleItemService.getSaleItemsBySaleId(saleId);
        
        // ApiResponse.success 형식으로 패킹하여 반환
        return ResponseEntity.ok(ApiResponse.success("Sale items retrieved successfully.", responseData));
    }

}
