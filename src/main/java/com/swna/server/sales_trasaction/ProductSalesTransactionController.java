package com.swna.server.sales_trasaction;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sales/products")
@RequiredArgsConstructor // 🔥 1. Lombok을 통한 생성자 자동 생성
public class ProductSalesTransactionController {

    // 🔥 2. private final 키워드를 추가하여 필드를 선언해야 합니다.
    private final ProductSalesTransactionService salesService;

    @GetMapping("/{barcode}")
    public ResponseEntity<List<ProductSalesTransactionData>> getSalesByPeriod(
            @PathVariable String barcode,
            @RequestParam(defaultValue = "monthly") String period,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        
        List<ProductSalesTransactionData> result = switch (period.toLowerCase()) {
            case "weekly" -> salesService.getWeeklySales(barcode, startDate);
            case "quarterly" -> salesService.getQuarterlySales(barcode, startDate);
            default -> salesService.getMonthlySales(barcode, startDate);
        };

        return ResponseEntity.ok(result);
    }
}