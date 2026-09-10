package com.swna.server.sales_trasaction;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductSalesTransactionService {

    private final ProductSalesTransactionRepository salesRepository;

    public List<ProductSalesTransactionData> getWeeklySales(String barcode, LocalDate startDate) {
        List<Object[]> results = salesRepository.findWeeklySales(barcode, startDate);
        return results.stream()
                .map(result -> {
                    String yearWeekStr = (String) result[1];
                    YearWeekToLocalDate yearWeek = YearWeekToLocalDate.parse(yearWeekStr);
                    LocalDate start = yearWeek.toLocalDate();
                    int count = ((Number) result[2]).intValue();
                    return new ProductSalesTransactionData((String) result[0], start.toString(), count);
                })
                .collect(Collectors.toList());
    }

    public List<ProductSalesTransactionData> getMonthlySales(String barcode, LocalDate startDate) {
        List<Object[]> results = salesRepository.findMonthlySales(barcode, startDate);
        return results.stream()
                .map(result -> {
                    int count = ((Number) result[2]).intValue();
                    return new ProductSalesTransactionData((String) result[0], (String) result[1], count);
                })
                .collect(Collectors.toList());
    }

    public List<ProductSalesTransactionData> getQuarterlySales(String barcode, LocalDate startDate) {
        List<Object[]> results = salesRepository.findQuarterlySales(barcode, startDate);
        return results.stream()
                .map(result -> {
                    int count = ((Number) result[2]).intValue();
                    return new ProductSalesTransactionData((String) result[0], (String) result[1], count);
                })
                .collect(Collectors.toList());
    }
}
